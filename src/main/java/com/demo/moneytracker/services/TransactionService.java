package com.demo.moneytracker.services;

import java.util.List;

import com.demo.moneytracker.entities.Transaction;
import com.demo.moneytracker.entities.User;


public interface TransactionService {
	
	
	// This is dashboard methods 

	Double getTotalBorrowed(Integer userId);

	Double getTotalLent(Integer userId);

	List<Transaction> getRecentTransactions(Integer userId, int limit);

	
	// This is transaction methods 
	
	void save(Transaction transaction);

	List<Transaction> findByUserId(Integer userId);

	List<Transaction> searchTransactions(Integer userId, String keyword);

	Transaction findById(Long id);

	void updateTransactionStatus(Transaction txn, User user, String string);

	
}
