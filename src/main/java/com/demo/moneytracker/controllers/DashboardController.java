package com.demo.moneytracker.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;
import com.demo.moneytracker.entities.Transaction;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.services.TransactionService;
import com.demo.moneytracker.services.UserService;

// This is a DashboardController
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
	
	 @Autowired
	  UserService userService;
	 
	 @Autowired
	 TransactionService transactionService;


		/*
		 * @GetMapping("/") public String showDashboardPage() { return "dashboard"; //
		 * show the jsp dashboard form }
		 */
	
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
	    // Destroy the session
	    session.invalidate();

	    // Redirect to login page
	    return "redirect:/login/";     // This will redirect me to login page after logout 
	}
	
	
	
	@GetMapping("/")
	 public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		
		
		 // Get logged-in user
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        if (loggedInUser == null) {
        	redirectAttributes.addFlashAttribute("error", "Session time out please try login again ");
            return "redirect:/login/";
        }
		
        
        Integer userId = loggedInUser.getId(); // doesn’t hit the DB it just gives the already-loaded ID.Later, that ID is used in repository queries to fetch this user’s transactions.
        
        // 1. Get Total Balance
        Double balance = userService.getUserBalance(userId);
        model.addAttribute("balance", balance);
        
        
        // 2. Get Total Borrowed
        Double borrowedAmount = transactionService.getTotalBorrowed(userId);  
        model.addAttribute("borrowedAmount", borrowedAmount);
        
        
        
        // 3. Get Total Lent
        Double lentAmount = transactionService.getTotalLent(userId);
        model.addAttribute("lentAmount", lentAmount);
        
        
        
        // 4. Get Recent Transactions (last 5 for example)
        List<Transaction> recentTransactions = transactionService.getRecentTransactions(userId, 5);
        model.addAttribute("recentTransactions", recentTransactions);

        return "dashboard";
		 
		 
		 
		 
	 }
	
	
	
	
	
	
	
	
	
	
	
	
}
