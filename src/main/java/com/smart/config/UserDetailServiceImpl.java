package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;


public class UserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	//private User user;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
	// fetching user from database
		User user = userRepository.getUserByUserName(username);
		//System.out.println("Custom User Detail "+ username);
		
		if(user==null)
		{
			//System.out.println("Custom User Detail ERROR "+ username);
			throw new UsernameNotFoundException("User doesn't exists");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		return customUserDetails;
		
	}
	

}
