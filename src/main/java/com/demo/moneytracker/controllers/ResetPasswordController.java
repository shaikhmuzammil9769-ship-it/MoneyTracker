package com.demo.moneytracker.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.exceptionhandler.InputValidationException;
import com.demo.moneytracker.services.UserService;

@Controller
@RequestMapping("/resetpassword")
public class ResetPasswordController {

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String showResetPasswordPage() {
        return "resetpassword";
    }

    @PostMapping
    public String handleResetPassword(@RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      HttpSession session,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        // Step 1: Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "resetpassword";
        }

        // Step 2: Validate password pattern
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%?&])[A-Za-z\\d@$!%?&]{10,}$";
        if (!newPassword.matches(passwordPattern)) {
            model.addAttribute("error",
                "Password must be at least 10 characters long, and include uppercase, lowercase, digit, and special character.");
            return "resetpassword";
        }

        // Step 3: Get session input (email or mobile)
        String input = (String) session.getAttribute("otpInput");
        if (input == null) {
            model.addAttribute("error", "Session expired or OTP timed out. Please request a new OTP.");
            return "forgotpassword";
        }

        try {
            // Step 4: Find user
            User user = userService.findByEmailOrMobile(input);
            if (user == null) {
                model.addAttribute("error", "User not found.");
                return "resetpassword";
            }

            // Step 5: Check if new password is same as current
            if (newPassword.equals(user.getPassword())) {
                throw new InputValidationException("New password cannot be same as the current password.");
            }

            // Step 6: Update and save user (with encoding)
            user.setPassword(newPassword); // or passwordEncoder.encode(newPassword)
            userService.saveuser(user);

            session.invalidate(); // clear session

            redirectAttributes.addFlashAttribute("success", "Password reset successful. Please log in.");
            return "redirect:/login/";

        } catch (InputValidationException ex) {
            model.addAttribute("error", ex.getMessage());
            return "resetpassword";
        }
    }
}
