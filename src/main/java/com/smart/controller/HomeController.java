package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
		@Autowired
		private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository; 
	
	@RequestMapping("/")
	public String Home(Model model)
	{
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String About (Model model)
	{
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title","Register- Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	
	// handler for registering user
	@RequestMapping(value="/doregister", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,
												BindingResult result1,@RequestParam(value="enabled", defaultValue = "false") boolean accept,
												Model model, HttpSession session   )
	{
		try {
			
				if(!accept)
				{
					System.out.println("Not Accepted");
					throw new Exception("Not Accepted"+ accept);
				}
				//binding result with errors checking 
				if(result1.hasErrors())
				{
					System.out.println("Binding Result Error "+result1.toString());
					model.addAttribute("user", user);
					return "signup";
				}
				user.setRole("ROLE_USER");
				user.setEnabled(true);
				user.setImageUrl("default.png");
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				System.out.println("PAssword:"+passwordEncoder.encode(user.getPassword()));
				// verify email already exists .. can find ....if (this.userRepository.exists("email",user.getEmail());
				this.userRepository.save(user);
				System.out.println("Agreement Accpeted" + accept);
				System.out.println("User" + user);
				model.addAttribute("user", user);
				model.addAttribute("user", new User());
				session.setAttribute("message", new Message("User Added! Successfuly Registered","alert-success"));
				//return "signup";
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);		
			session.setAttribute("message", new Message("Something went wrong "+ e.getMessage(),"alert-danger"));
			//return "signup";
		}
		return "signup";
	}

	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}
}
