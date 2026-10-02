package com.kedu.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.kedu.dao.MemberDAO;
import com.kedu.dao.PartyDAO;
import com.kedu.dto.MemberDTO;
import com.kedu.dto.PartyApplicationDTO;
import com.kedu.dto.PartyDTO;

@Controller
@RequestMapping("/party")
public class PartyController {

	@Autowired
	private PartyDAO partyDAO;
	
	@Autowired
	private MemberDAO memberDAO;

	@Autowired
	private Gson gson;

	@RequestMapping("/list")
	public String list(Model model) {
		List<PartyDTO> parties = partyDAO.findAll(0);
		model.addAttribute("parties", parties);
		return "party/list";
	}

	@ResponseBody
	@RequestMapping(value = "/more", produces = "application/json; charset=UTF-8")
	public String more(@RequestParam int offset) {
		List<PartyDTO> parties = partyDAO.findAll(offset);
		return gson.toJson(parties);
	}

	@RequestMapping("/detail")
	public String derail(int partyId, Model model) {
		PartyDTO party = partyDAO.findById(partyId);

		if (party == null) {
			return "redirect:/party/list";
		}
		int memberCount = partyDAO.countMembers(partyId);
		model.addAttribute("memberCount", memberCount);

		List<String> memberNames = partyDAO.findMemberNames(partyId);
		model.addAttribute("memberNames", memberNames);

		MemberDTO host = memberDAO.selectMember(party.getHostId());
		model.addAttribute("hostName", host.getUsername());
	
		model.addAttribute("party", party);
		return "party/detail";
	}

	@RequestMapping("/create")
	public String create() {
		return "party/create";
	}

	@RequestMapping("/apply")
	public String apply(int partyId, HttpSession session, Model model) {
		PartyDTO party = partyDAO.findById(partyId);

		String loginId = (String) session.getAttribute("loginId");
		
		if (loginId == null) {
			return "redirect:/member/login";
		}
		
		PartyDTO dto = partyDAO.findById(partyId);
		
		if (dto == null) {
			return "redirect:/party/list";
		}
		
		if (party == null) {
			return "redirect:/party/list";
		}
		
		model.addAttribute("party", dto);
		return "party/apply";

	}

	@RequestMapping(value = "/createSubmit", method = RequestMethod.POST)
	public String createSubmit(PartyDTO dto, @RequestParam("meetDateText") String meetDateText, HttpSession session,
			Model model) {

		String loginId = (String) session.getAttribute("loginId");
		if (loginId == null) {
			return "redirect:/member/login";
		}

		dto.setHostId(loginId);

		LocalDateTime meetDate = LocalDateTime.parse(meetDateText);
		dto.setMeetDate(Timestamp.valueOf(meetDate));

		String message = "";

		if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
			message += "모임 제목을 입력해 주세요.\n";
		} else {
			dto.setTitle(dto.getTitle().trim());
		}

		if (dto.getMinPeople() < 2 || dto.getMaxPeople() < dto.getMinPeople()) {
			message += "모임의 최소 인원은 2명이며, 최대 인원은 최소 인원 이상이어야 합니다.\n";

		}

		if (dto.getMinAge() != null && dto.getMaxAge() != null && dto.getMinAge() > dto.getMaxAge()) {
			message += "최대 나이는 최소 나이 이상이어야 합니다.\n";

		}

		if (!meetDate.isAfter(LocalDateTime.now())) {
			message += "모임 날짜는 현재 시간 이후로 선택해 주세요.\n";
		}

		if (!message.isEmpty()) {
			model.addAttribute("message", message);
			model.addAttribute("party", dto);
			model.addAttribute("meetDateText", meetDateText);
			return "party/create";
		}

		int partyId = partyDAO.createParty(dto);

		return "redirect:/party/detail?partyId=" + partyId;
	}

	@RequestMapping(value = "/applySubmit", method = RequestMethod.POST)
	public String applySubmit(@RequestParam("partyId") int partyId,
			@RequestParam(value = "answer", defaultValue = "") String answer,
			@RequestParam(value = "agree", defaultValue = "") String agree, HttpSession session, Model model) {

		String loginId = (String) session.getAttribute("loginId");

		if (loginId == null) {
			return "redirect:/member/login";
		}

		try {
			if (!"Y".equals(agree)) {
				throw new IllegalArgumentException("모임 규칙 및 노쇼 방지 안내에 동의해주세요.");
			}
			
			PartyDTO dto = partyDAO.findById(partyId);
			
			if(dto == null) {
				return "redirect:/party/list";
			}
			
			MemberDTO member = memberDAO.selectMember(loginId);
			
			if("male".equals(dto.getGenderRule())
				&& !"남성".equals(member.getGender())) {
					throw new IllegalArgumentException("남성만 참여할 수 있는 모임입니다.");
				}
			
			if("female".equals(dto.getGenderRule())
			&& !"여성".equals(member.getGender())) {
			 throw new IllegalArgumentException("여성만 참여할 수 있는 모임입니다.");
			}
			
			int age = Period.between(member.getBirth_date().toLocalDate(),
					LocalDate.now()
					).getYears();
				
					if(dto.getMinAge() !=null && age < dto.getMinAge()) {
					throw new IllegalArgumentException("최소" + dto.getMinAge() + "세부터 참여할 수 있습니다.");	
			}
					
					if(dto.getMaxAge() !=null && age > dto.getMaxAge()) {
						throw new IllegalArgumentException("최대" + dto.getMaxAge() + "세까지 참여할 수 있습니다.");
					}
			
			partyDAO.apply(partyId, loginId, answer);
			return "redirect:/party/detail?partyId=" + partyId;

		} catch (IllegalArgumentException e) {
			PartyDTO dto = partyDAO.findById(partyId);

			if (dto == null) {
				return "redirect:/party/list";
			}
			model.addAttribute("party", dto);
			model.addAttribute("message", e.getMessage());
			model.addAttribute("answer", answer);

			return "party/apply";
		}
	}

	@RequestMapping("/applications")
	public String applications(int partyId, HttpSession session, Model model) {
		PartyDTO dto = partyDAO.findById(partyId);

		if (dto == null) {
			return "redirect:/party/list";
		}

		String loginId = (String) session.getAttribute("loginId");

		if (loginId == null) {
		    return "redirect:/member/login";
		}
		
		if (!loginId.equals(dto.getHostId())) {
			return "redirect:/party/list";
		}

		List<PartyApplicationDTO> applications = partyDAO.findPendingApplications(partyId);

		model.addAttribute("party", dto);
		model.addAttribute("applications", applications);

		return "party/applications";
	}

	@RequestMapping(value = "/approve", method = RequestMethod.POST)
	public String approve(@RequestParam("partyId") int partyId, @RequestParam("applicationId") int applicationId,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		String loginId = (String) session.getAttribute("loginId");

		if (loginId == null) {
		    return "redirect:/member/login";
		}

		try {
			partyDAO.approve(partyId, applicationId, loginId);
			redirectAttributes.addFlashAttribute("message", "승인했습니다.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/party/applications?partyId=" + partyId;
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST)
	public String reject(@RequestParam("partyId") int partyId, @RequestParam("applicationId") int applicationId,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		String loginId = (String) session.getAttribute("loginId");

		if (loginId == null) {
		    return "redirect:/member/login";
		}

		try {
			partyDAO.reject(partyId, applicationId, loginId);
			redirectAttributes.addFlashAttribute("message", "거절했습니다.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/party/applications?partyId=" + partyId;
	}
}
