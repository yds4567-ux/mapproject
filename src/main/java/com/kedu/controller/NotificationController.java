package com.kedu.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kedu.dao.NotificationDAO;
import com.kedu.dto.NotificationDTO;

@Controller
@RequestMapping("/notification")
public class NotificationController {
	
	@Autowired
	private NotificationDAO notificationDAO;
	
	@RequestMapping("/list")
	public String list(HttpSession session, Model model) {
		String loginId = (String) session.getAttribute("loginId");
		
		if(loginId == null) {
			return "redirect:/member/login";
		}
		
		List<NotificationDTO> notifications = 
				notificationDAO.findByReceiverId(loginId);
		
		model.addAttribute("notifications", notifications);
		
		return "notification/list";
	}

	@RequestMapping(value="/read", method = RequestMethod.POST)
	public String read(
			@RequestParam("notificationId") int notificationId, HttpSession session) {
		
		String loginId = (String) session.getAttribute("loginId");
		
		if (loginId == null) {
			return "redirect:/member/login";
		}
		notificationDAO.markAsRead(notificationId, loginId);
		
		return "redirect:/notification/list";
	}
	
	
}
