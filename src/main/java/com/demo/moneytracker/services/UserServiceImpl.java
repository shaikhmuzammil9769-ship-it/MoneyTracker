package com.demo.moneytracker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.moneytracker.entities.User;
import com.demo.moneytracker.exceptionhandler.InputValidationException;
import com.demo.moneytracker.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	UserRepository userRepository;

	
	// This is my register user methods
	
	@Override
	public boolean isUsernameTaken(String username) {
		
		
		return userRepository.existsByUsername(username);
	}

	@Override
	public void saveuser(User user) {

		userRepository.save(user);
		
		
	}
	
	// This is my forgotpassword Methods 
	
	public User findByEmailOrMobile(String input) {
		
	    if (input.matches("^\\d+$")) {   // This will check if the input is string/number  [mix is not allowed like abc123]
	    	
	        // Looks like numbers → maybe mobile
	        if (!input.matches("^\\d{10}$")) {          // (^) start and ($) means end (//d) means digit and (+) means one or more [This is not allowed 999999999999]
	        	
	        	
	            throw new InputValidationException("Invalid mobile number format.");
	        }
	        try {
	            Long mobile = Long.parseLong(input);
	            return userRepository.findByMobile(mobile);
	        } catch (NumberFormatException e) {
	            throw new InputValidationException("Mobile number is not valid.");
	        }

	    } else if (input.contains("@")) {                // This should have a proper email name [@12.gmail / username@ are not allowed]
	    	
	        // Looks like email
	        if (!input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
	            throw new InputValidationException("Invalid email format.");
	        }
	        return userRepository.findByEmail(input);

	    } else {
	        // Doesn't look like email or mobile at all
	        throw new InputValidationException("Please enter a valid 10-digit mobile number or a valid email address.");
	    }
	}

	
	// This is my login method
	
	@Override
	public User findByUsername(String username) {
	    return userRepository.findByUsername(username);
	}
	
	
	// This is my Dashboard methods 

	@Override
	public Double getUserBalance(Integer userId) {
		
		User user = userRepository.findById(userId).orElse(null);
		
		return (user != null && user.getBalance() != null) ? user.getBalance() : 0.0; //if both the value is true it will fetch the balace 
		                                                                               //of user and if it is null it will return 0.0 so to avoid null pointer exception 
		
		
	}



}
