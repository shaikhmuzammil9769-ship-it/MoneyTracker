package com.demo.moneytracker.controllers;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.moneytracker.entities.Transaction;
import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.exceptionhandler.InsufficientBalanceException;
import com.demo.moneytracker.exceptionhandler.UserNotFoundException;
import com.demo.moneytracker.services.TransactionService;

@Controller
@RequestMapping("/transaction")
public class TransactionController {
	
	@Autowired
	TransactionService transactionService;
	
	 @GetMapping("/")
	    public String showAddForm(Model model) {
	        model.addAttribute("transaction", new Transaction());  // This will create a blank object of transaction entity and return to the jsp
	        return "addtransaction"; // JSP name
	    }

	 
	 @PostMapping("/save")
	    public String saveTransaction(@ModelAttribute Transaction transaction, HttpSession session, Model model) {
	        User user = (User) session.getAttribute("loggedInUser");
	        if (user == null) {
	            return "redirect:/login/";
	        }

	        
	     //   the transaction Every transaction is tied to the correct user.

	    //    Users can’t assign transactions to someone else.

	        transaction.setUser(user);       // Transaction will try to save user_id in the transaction db otherwise it will save it as null and error might throw
	        transaction.setCreatedAt(LocalDateTime.now());  // which gives the current server time when the transaction is saved.
	        
	        
	   //     setUser(user) → attaches the transaction to the logged-in user (ownership).

	  //      setCreatedAt(LocalDateTime.now()) → stamps the transaction with the creation time (audit/history).

	        try {
	            transactionService.save(transaction);   
	            return "redirect:/dashboard/";
	        } catch (UserNotFoundException | InsufficientBalanceException e) {
	            model.addAttribute("errorMessage", e.getMessage());
	            model.addAttribute("transaction", transaction); // keep filled form
	            return "addtransaction"; // stay on transaction page with error
	        }

	     
	    
	 
	 }
	 
	 
	 
	 
	 @GetMapping("/list")
	 public String listTransactions(HttpSession session, Model model) {
	     User user = (User) session.getAttribute("loggedInUser");
	     if (user == null) {
	            return "redirect:/login/";
	        }
	     // Fetch all transactions for this user
	     model.addAttribute("transactions", transactionService.findByUserId(user.getId()));
	     return "viewTransactions"; // JSP name
	 }
	 
	 
	 
	 
	 @GetMapping("/searchTransactions")
	 public String searchTransactions(@RequestParam("keyword") String keyword,
	                                  HttpSession session, Model model) {
	     User user = (User) session.getAttribute("loggedInUser");
	     if (user == null) {return "redirect:/login/";}
	     

	     List<Transaction> results = transactionService.searchTransactions(user.getId(), keyword);

	     model.addAttribute("searchResults", results);

	     // Also load dashboard stats (so page won’t break)
	     model.addAttribute("balance", user.getBalance());
	     model.addAttribute("borrowedAmount", transactionService.getTotalBorrowed(user.getId()));
	     model.addAttribute("lentAmount", transactionService.getTotalLent(user.getId()));

	     return "dashboard"; // reload dashboard with results
	 }
	 
	 
	 
	 
	 
	 @RequestMapping("/markPaid")
	    public String markPaid(@RequestParam Long id, HttpSession session) {
	        User user = (User) session.getAttribute("loggedInUser");
	        if (user == null) {
	        	return "redirect:/login/";
	        	}
	        

	        Transaction txn = transactionService.findById(id);
	        if (txn != null && txn.getUser().getId().equals(user.getId())) {
	            transactionService.updateTransactionStatus(txn, user, "Paid");
	        }
	        return "redirect:/dashboard/";
	    }

	    @RequestMapping("/markPending")
	    public String markPending(@RequestParam Long id, HttpSession session) {
	        User user = (User) session.getAttribute("loggedInUser");
	        if (user == null) return "redirect:/login";

	        Transaction txn = transactionService.findById(id);
	        if (txn != null && txn.getUser().getId().equals(user.getId())) {
	            transactionService.updateTransactionStatus(txn, user, "Pending");
	        }
	        return "redirect:/dashboard/";
	    }
	}

	 
	 

