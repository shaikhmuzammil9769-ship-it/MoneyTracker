package com.demo.moneytracker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.moneytracker.entities.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
	
	
    List<Person> findByUser_Id(Integer userId);  //  Data JPA will generate query based on this method and returns the response with user_id List
}
