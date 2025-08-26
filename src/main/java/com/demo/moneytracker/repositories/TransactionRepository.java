package com.demo.moneytracker.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.moneytracker.entities.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	// Get total amount for Borrow or Lend for a user
	@Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = ?1 AND t.user.id = ?2")
	Double sumByTypeAndUserId(String type, Integer userId);

	// Get recent transactions for a user
	@Query("SELECT t FROM Transaction t WHERE t.user.id = ?1 ORDER BY t.createdAt DESC")
	List<Transaction> findRecentTransactions(Integer userId, Pageable pageable);

	// To get the list of all transactions for a particular User
	@Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
	List<Transaction> findByUserId(Integer userId);

	// This will searh the user on the bases of keyword (personName,amount,status,type)
	@Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND " +
		       "(LOWER(t.personName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR STR(t.amount) LIKE CONCAT('%', :keyword, '%') " +
		       "OR LOWER(t.status) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR LOWER(t.type) LIKE LOWER(CONCAT('%', :keyword, '%')) )")
		List<Transaction> searchTransactions( Integer userId,  String keyword);

}

// This is the query that hibernate will generate after mapping Transaction class to the tabel inside that class and translate jpql->sql qury below :

//SELECT SUM(t.amount)
//FROM transactions t
//JOIN register_user u ON t.user_id = u.id
//WHERE t.type = 'Borrow' AND u.id = 5;


// ORDER BY t.createdAt DESC" -> This means order by most recent and in descanding order which has the lastes transaction 