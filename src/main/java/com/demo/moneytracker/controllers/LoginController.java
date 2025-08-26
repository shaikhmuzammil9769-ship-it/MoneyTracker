package com.demo.moneytracker.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.services.UserService;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	 @Autowired
	  UserService userService;
	
    @RequestMapping("/")
    public String showLoginPage() {
        return "login"; 
}
    
    
    
    @PostMapping("/process")
    public String handleLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              HttpSession session,
                              Model model) {

        // Step 1: Find user by username
    	User user = userService.findByUsername(username.trim());
    	
    	
    	// Debug the application 
//    	System.out.println("Login attempt -> username: " + username + ", password entered: " + password);
//        if (user != null) {
//            System.out.println("User found in DB -> " + user.getUsername() + ", DB password: " + user.getPassword());
//        } else {
//            System.out.println("No user found in DB for username: " + username);
//        }

        // Step 2: Validate credentials
        if (!(user != null && user.getPassword().equals(password))) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }


        // Step 3: Valid Login
        session.setAttribute("loggedInUser", user);             // store full user object
        session.setAttribute("username", user.getUsername());   // used in dashboard.jsp
        session.setAttribute("userId", user.getId());           // used for DB queries

        // Optional: Session timeout in seconds
        // session.setMaxInactiveInterval(600); // 10 minutes

        return "redirect:/dashboard/";
    }
    
}