package com.demo.moneytracker.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.demo.moneytracker.entities.Transaction;
import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.exceptionhandler.InsufficientBalanceException;
import com.demo.moneytracker.exceptionhandler.UserNotFoundException;
import com.demo.moneytracker.repositories.TransactionRepository;
import com.demo.moneytracker.repositories.UserRepository;


@Service
public class TransactionServiceImpl implements TransactionService{
	
	@Autowired
	TransactionRepository transactionRepository;
	

	@Autowired
	UserRepository userRepository;
	

	@Override
	public Double getTotalBorrowed(Integer userId) {
	    Double amount = transactionRepository.sumByTypeAndUserId("Borrow", userId);   //This calls a custom repository method sumByTypeAndUserId for borrowed
	    return amount != null ? amount : 0.0;
	}

	@Override
	public Double getTotalLent(Integer userId) {

		Double amount = transactionRepository.sumByTypeAndUserId("Lend", userId);   //  //This calls a custom repository method sumByTypeAndUserId for Lent
	    return amount != null ? amount : 0.0;
	}

	@Override
	public List<Transaction> getRecentTransactions(Integer userId, int limit) {
	    return transactionRepository.findRecentTransactions(userId, PageRequest.of(0, limit)); 
	   

// This pgaereq is a spring datajpa class which implements pageable Interface
//  This is use when we want a specific result of user record it has a method 
// called  .of(0,limit) 0 means the firstpage and in limit we have pass 5 users (Page 0 (first page) → returns 5 most recent/Page 1 (second page) → skips first 5, gets the next 5:)
	    
	    
	    
	// This method is for transaction to add    
	    
	    
	    
	}
	@Override
	public void save(Transaction transaction) {
	    // Fetch fresh user from DB
	    User dbUser = userRepository.findById(transaction.getUser().getId()) // getUser only have sssion id but when we do getId from getUser it comes from user entity 
	        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + transaction.getUser().getId()));

	    // Check balance
	    if ("Lend".equalsIgnoreCase(transaction.getType())) {
	    	 if ("Lend".equalsIgnoreCase(transaction.getType()) && dbUser.getBalance() < transaction.getAmount()) {
	    		        throw new InsufficientBalanceException("Insufficient balance! Available: ₹" + dbUser.getBalance());
	    		    }
	        dbUser.setBalance(dbUser.getBalance() - transaction.getAmount()); // Lending reduces balance
	    } else if ("Borrow".equalsIgnoreCase(transaction.getType())) {
	        dbUser.setBalance(dbUser.getBalance() + transaction.getAmount()); // Borrowing increases balance
	    }


	    // Save both
	    
	   transactionRepository.save(transaction); // Saves the Transaction record in DB (with personName, amount, type, status, createdAt, and linked user_id
	    
	   userRepository.save(dbUser);   //  // Updates the User record in DB (with the new balance).

	    
	}
	
	// This will return the list of transaction on viewtransaction button in desc order 

	@Override
	public List<Transaction> findByUserId(Integer userId) {
	    return transactionRepository.findByUserId(userId);
	}

//	findAll() fetches all transactions from the entire database, including transactions of other users. thats why we use find by userID
	
	
	
	
	// This will fetch the transaction on the bases of search name,amount,type
	
	public List<Transaction> searchTransactions(Integer userId, String keyword) {
	    return transactionRepository.searchTransactions(userId, keyword);
	}

	 @Override
	    public Transaction findById(Long id) {
	        return transactionRepository.findById(id).orElse(null);
	    }

	@Override
	public void updateTransactionStatus(Transaction txn, User user, String newStatus) {
        String oldStatus = txn.getStatus();

        // If no change, do nothing
        if (oldStatus.equalsIgnoreCase(newStatus)) {
            return;
        }

        double amount = txn.getAmount();

        if ("Lend".equalsIgnoreCase(txn.getType())) {
            if ("Paid".equalsIgnoreCase(newStatus)) {
                // Loan returned → Increase balance
                user.setBalance(user.getBalance() + amount);
            } else if ("Pending".equalsIgnoreCase(newStatus)) {
                // Loan still pending → Decrease balance
                user.setBalance(user.getBalance() - amount);
            }
        } 
        else if ("Borrow".equalsIgnoreCase(txn.getType())) {
            if ("Paid".equalsIgnoreCase(newStatus)) {
                // I paid money → Decrease balance
                user.setBalance(user.getBalance() - amount);
            } else if ("Pending".equalsIgnoreCase(newStatus)) {
                // Not yet paid → Increase balance
                user.setBalance(user.getBalance() + amount);
            }
        }

        txn.setStatus(newStatus);

        // Save both
        userRepository.save(user);
        transactionRepository.save(txn);
    }
}
	
	
	
	
	



