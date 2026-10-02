package com.kedu.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kedu.dao.PartyDAO;
import com.kedu.dao.SettlementDAO;
import com.kedu.dto.PartyDTO;

@Controller
@RequestMapping("/settlement")
public class SettlementController {

    @Autowired
    private PartyDAO partyDAO;

    @Autowired
    private SettlementDAO settlementDAO;

    @RequestMapping("/create")
    public String create(
            @RequestParam("partyId") int partyId,
            HttpSession session, Model model) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/member/login";
        }

        PartyDTO dto = partyDAO.findById(partyId);

        if (dto == null) {
            return "redirect:/party/list";
        }

        if (!loginId.equals(dto.getHostId())) {
            return "redirect:/party/detail?partyId=" + partyId;
        }

        model.addAttribute("party", dto);
        return "settlement/create";
    }

    @RequestMapping(value = "/createSubmit", method = RequestMethod.POST)
    public String createSubmit(
            @RequestParam("partyId") int partyId,
            @RequestParam("totalAmount") long totalAmount,
            HttpSession session, Model model) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/member/login";
        }

        try {
            settlementDAO.createEqual(partyId, loginId, totalAmount);
            return "redirect:/party/detail?partyId=" + partyId;

        } catch (IllegalArgumentException e) {
            PartyDTO dto = partyDAO.findById(partyId);

            if (dto == null) {
                return "redirect:/party/list";
            }

            model.addAttribute("party", dto);
            model.addAttribute("message", e.getMessage());

            return "settlement/create";
        }
    }
}
