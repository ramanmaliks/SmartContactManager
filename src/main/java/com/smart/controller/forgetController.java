package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

@Controller
public class forgetController {

	Random random = new Random(1000);
	@Autowired
	private EmailService emailService;
	//added to very email exist in record while otp process
	@Autowired
	private UserRepository userRepository;

	//added for encrypt new password while otp process
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@RequestMapping("/forget")
	public String openEmailForm()
	{
		return "forget_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session)
	{
	
		

		System.out.println("Email"+email);
		// Genertaing OTP of 4 digit
		
		
		int otp = random.nextInt(9999);
		System.out.println("OTP"+otp);
		
		//write code for send OTP to email.....
		
		String subject="OTP from SCM";
		String message="<HTML><BODY<><h1> OTP is "+ otp+"</h1></BODY></HTML>";
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else
		{
			
		session.setAttribute("message",new Message("Check you Email Address","alert-danger"));
		return "forget_email_form";
		}
	}
	
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session)
	{
		int myOtp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		
	 if(myOtp==otp)
	 {
		 User user = this.userRepository.getUserByUserName(email);
		 if(user==null)
		 {
			session.setAttribute("message", new Message ("Invalid User","alert-danger"));
			return "/forget_email_form";
		 }else {
		 //password change form
		 return "password_change_form";
		 
		 }}else
	 {
		 // wrong otp
		 session.setAttribute("message", new Message("Invalid OTP !!! try again","alert-danger"));
		 return "/verify_otp";
		 
	 }
	}

	@PostMapping("/update-password")
	public String psswdChange(@RequestParam("password") String newpasswd,HttpSession session) 
	{
		String email = (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bcrypt.encode(newpasswd));
		this.userRepository.save(user);
		 session.setAttribute("message", new Message("Password Changed!!!!","alert-success"));
		
		 return "redirect:/signin";
	}
}
