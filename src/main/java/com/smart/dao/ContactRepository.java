package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

//extends JpaRespository to fetch contact details of user
// JpaRespository <T,Id> (T Object ) to be Retrieve = Contact,
// JpaRepository <T,Id> Id type is Integer
public interface ContactRepository extends JpaRepository<Contact,Integer>{
// pagination
//Method to retrieve	contacts for a specified userId
	// pageable object will have current page  and contacts per page eg.5 per page
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageAble );
	
	//method without pagination
	//public List<Contact> findContactsByUser(@Param("userId") int userId);
	
	//search for contact from search bar
	public List<Contact> findByNameContainingAndUser(String keywords,User user);
	
}
