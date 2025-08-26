package com.demo.moneytracker.services;

import com.demo.moneytracker.entities.User;

public interface UserService {
	
	
	// This is register user methods 

	boolean isUsernameTaken(String username);     

	void saveuser(User user);
	
	// This is forgot password method

	User findByEmailOrMobile(String input);
	
	// This is Login method

	User findByUsername(String username);
	
	// This is dashboard method

	Double getUserBalance(Integer userId);

}
