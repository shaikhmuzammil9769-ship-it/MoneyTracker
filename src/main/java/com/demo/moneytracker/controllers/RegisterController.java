package com.demo.moneytracker.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.services.UserService;

@Controller
@RequestMapping("moneytracker/register")
public class RegisterController {

	@Autowired
	UserService userservice;
	
	@RequestMapping("/")
	public String showRegisterPage() {
	    return "register";
	}

	@PostMapping("/insert")
	public String handlerRegister(@ModelAttribute User user, HttpServletRequest req, Model model,  RedirectAttributes redirectAttributes) {

		String confirmpassword = req.getParameter("confirmPassword"); 

		if (userservice.isUsernameTaken(user.getUsername())) {

			model.addAttribute("error", "Username already exists.");

			return "register";

		}
		if (!user.getPassword().equals(confirmpassword)&& user.getPassword().matches("\"^(?=.[A-Z])(?=.[a-z])(?=.\\\\d)(?=.[@$!%?&])[A-Za-z\\\\d@$!%?&]{10,}$\"")) {

			model.addAttribute("error", "Passwords do not match.");

			return "register";
		}

		userservice.saveuser(user);

		redirectAttributes.addFlashAttribute("success", "Account created successfully. You can now log in.");

		return "redirect:/login/";
	}

}
