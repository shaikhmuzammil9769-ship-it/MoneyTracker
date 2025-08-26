package com.demo.moneytracker.controllers;

import java.security.SecureRandom;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.exceptionhandler.InputValidationException;
import com.demo.moneytracker.services.UserService;

@Controller
@RequestMapping("/forgotpassword")
public class ForgotPasswordController {

	@Autowired
	UserService userservice;

	@RequestMapping("/")
	public String showForgotPasswordPage() {
		return "forgotpassword"; // returns views/forgotpassword.jsp
	}

	@PostMapping("/process")
	public String processForgotPassword(@RequestParam("input") String input,
			@RequestParam(value = "otpEntered", required = false) String otpEntered, HttpSession session, Model model) {

		// Step 2 // After submitting (email/mobile)

		if (otpEntered != null) {

			String storedOtp = (String) session.getAttribute("otp"); // getAttribute() method always return an object
																		// and he does not know the type you have to
																		// caste it.

			String storedInput = (String) session.getAttribute("otpInput");

			if (storedOtp != null && storedOtp.equals(otpEntered) && input.equals(storedInput)) {
				
				// This basically removes/empty the session and the user will not able to use the old OTP and email/mobile no 
				
			//	 session.removeAttribute("otp");
		      //      session.removeAttribute("otpInput");

				return "redirect:/resetpassword/";

			} else {

				model.addAttribute("error", "Invalid OTP. Please try again.");
				model.addAttribute("otp", storedOtp); // Show OTP (for demo/ This will display the OTP on UI)
				model.addAttribute("showOtp", true); // Show OTP input box (This will show the OTP Box on UI)
				  model.addAttribute("input", input); // This will let my old value on the screen when the OTP fails[This is related to ${input} from front-end ]
				return "forgotpassword";
			}

		}

		// step 1 : First form submission (email/phone)

		try {
			
		User user = userservice.findByEmailOrMobile(input);
		if (user != null){
			
		
			String otp = String.valueOf(1000 + new SecureRandom().nextInt(9000)); // 4-digit

			session.setAttribute("otp", otp); // This will store in session for temporary
			session.setAttribute("otpInput", input); // stores the user’s email or mobile
			session.setMaxInactiveInterval(120);  // 2 mins only for this session

			model.addAttribute("otp", otp); // Show Otp on form
			model.addAttribute("showOtp", true); // This will allow the jsp to enter if block in jsp from to show otp in
													// UI
		}	
		
		
			else {

			model.addAttribute("error", "Email or mobile number is not registered.");
			 model.addAttribute("input", input); //  This will let my old value on the screen even it doesn't find user
			
			}
		}
		catch (InputValidationException ex) {
		   
		    model.addAttribute("error", ex.getMessage());  // 🚨 This handles your custom validation errors from service layer
		    model.addAttribute("input", input); // preserve input [This will let my old value on the screen even it doesn't find user]
		}
		
		return "forgotpassword";

	}

}
