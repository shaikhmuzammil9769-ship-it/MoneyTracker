package com.demo.moneytracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.moneytracker.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

	boolean existsByUsername(String username);  // Data JPA will generate query based on this method and returns the response with true/false

	User findByMobile(Long mobile);      // Data JPA on will generate query based on this method and return the user on the bases of mobile 

	User findByEmail(String input);    //  Data JPA on will generate query based on this method and return the user on the bases of email

	User findByUsername(String username); // Data JPA on will generate query based on this method and return the user on the bases of username;

}
