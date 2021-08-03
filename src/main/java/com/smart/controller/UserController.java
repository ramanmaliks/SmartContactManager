package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//for common adding user data for every Request Mapping Reponse
	@ModelAttribute
	private void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.print("user name : "+ userName);
		// Fetchin the user details using username from db
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER "+user);
		model.addAttribute("user",user);		
	}
	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal)
	{
		return "user/user_dashboard";
	}

	//ADD FORM HANDLER
	@GetMapping("/addcontact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Form");
		model.addAttribute("contact", new Contact());
		
		return "user/add_contact_form";
	}
	
	//processing add contact form handler
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact ,
														BindingResult result,
													 @RequestParam("profileimage") MultipartFile file, 
													 Principal principal,Model model,HttpSession session ) {
		try {
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		//binding result with errors checking 
		if(result.hasErrors())
		{
			
			System.out.println("Binding Result Error "+result.toString());
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Please Check ! Errors ","alert-danger"));
			return "user/add_contact_form";
		}
		//processing and uploading file.
		if(file.isEmpty()) {
			System.out.println("file is empty");
			//assigning default photo for empty file
			contact.setImage("default.png");
		}
		else {
			// file the file to folder and update the name to contact
			Random random = new Random(1000);
			int randomWithNextInt = random.nextInt(9999);
			
			String n2  = String.valueOf(randomWithNextInt);
			
					n2 = n2+"_"+file.getOriginalFilename();
			
			contact.setImage(n2);
			File saveFile = new ClassPathResource("static/image").getFile();
//			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+name+"_"+file.getOriginalFilename());
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+n2);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image Uploaded");
		}
		
		contact.setUser(user);
		user.getContacts().add(contact);

		
		this.userRepository.save(user);
		System.out.println("Contact Data" +  contact );
		System.out.println("contact added");
		session.setAttribute("message", new Message("New Contact Added! Successfuly Registered","alert-success"));
		
		} catch (Exception e)
		{
			System.out.println("ERROR in Adding a Contact "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("contact", contact);	
			session.setAttribute("message", new Message("Please Check ! Errors "+ e.getMessage(),"alert-danger"));
			//return "user/add_contact_form";	
		}
		return "user/add_contact_form";	
	}
	
	//VIEW CONTACT HANDLER
	//PER PAGE = 5[N] CONTACT PER PAGE
	//CURRENT PAGE = 0 [PAGE[ USER ON WHICH PAGE
	//page added int getmapping for know user which user request the current page
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title","VIEW CONTACTS || SMARTCONTACT MANAGER");
// simple method for reterive list
//		//*****Retrieve contact list
//		// Retrieving User Name i.e. email 
//		String userName = principal.getName();
//		// Retrieving  User Detail for id
//		User user = this.userRepository.getUserByUserName(userName);
//		// Retrieving all contact list of a user
//		List<Contact> contacts = user.getContacts();
		
		// *** Using Repository Method  for Retrieving Contact List
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
	// PageRequest.of(page,5) store 5 contacts of a user per page
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "user/show_contacts";
	}

	//showing user's a contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal)
	{
		System.out.println("*********************************Contact id ="+cId);
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		if(contactOptional.isEmpty()) {
			return "user/contact_detail";	
		}
		
		Contact contact = contactOptional.get();
		
		
		//security for user who is login
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId()) 
			model.addAttribute("contact", contact);
		
		
		return "user/contact_detail";
		
		
	}
	
	
//delete a contact handler
	@RequestMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model,HttpSession session,Principal prinicipal)
	{
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		//		if(contactOptional.isEmpty()) {
		//			return " ";
		//		}
		Contact contact = contactOptional.get();
		//contact.setUser(null);
		User user = this.userRepository.getUserByUserName(prinicipal.getName());
		
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		//this.contactRepository.delete(contact);
		
		session.setAttribute("message", new Message("Contact deleted Successfull...."+contact.getcId(),"alert-success"));
		return "redirect:/user/show-contacts/0";
		
	}
	
// Update Contact retrieve data from db Form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId,Model m)
	{
		m.addAttribute("title","Update Contact Form");
		Contact contact = this.contactRepository.findById(cId).get();
		m.addAttribute("contact", contact);
		return "user/update_form";
		
	}
	
	
// update contact in dabatabe form handler
	@RequestMapping(value="/process-update", method = RequestMethod.POST)			
	public String updateHandler(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Model m,
			HttpSession session, Principal principal) 
	{
		try {
			Contact contactolddetail = this.contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty())
				{
				//file work rewrite
				//Delete old photo
				File oldFile = new ClassPathResource("static/image").getFile();
				File deleteFile = new File(oldFile, contactolddetail.getImage());
				deleteFile.delete();
				
				//Upload new file
				Random random = new Random(1000);
				int randomWithNextInt = random.nextInt(9999);
			
				String n2  = String.valueOf(randomWithNextInt);				
				n2 = n2+"_"+file.getOriginalFilename();
				contact.setImage(n2);
				File saveFile = new ClassPathResource("static/image").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+n2);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					
				}else
			{
				contact.setImage(contactolddetail.getImage());
			}
		
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message",new Message("Your contact is updated....","alert-success"));
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		System.out.println("Contact Name : " + contact.getName());
		System.out.println("Contact ID : " + contact.getcId());
		return "redirect:/user/"+contact.getcId()+"/contact";
		
	
	}
	
	// user profile handler
	@GetMapping("/profile")
	public String userProfile(Model model)
	{
		model.addAttribute("title","Profile Page");
		return "user/profile";
	}
	
	
	//Edit User profile Controller
	@PostMapping("/update-user/{id}")
	public String updateUserForm(@PathVariable("id") Integer Id, Model m)
	{
		m.addAttribute("title","User Update Contact Form");
		User user = this.userRepository.findById(Id).get();
		m.addAttribute("user", user);
		return "user/update_userform";
		
	}

	// update contact in dabatabe form handler
		@RequestMapping(value="/process-userupdate", method = RequestMethod.POST)			
		public String updateUserHandler(@ModelAttribute User user,
				@RequestParam("profileImage") MultipartFile file, Model m,
				HttpSession session, Principal principal) 
		{
			try {
				User userolddetail = this.userRepository.findById(user.getId()).get();
				//Contact contactolddetail = this.contactRepository.findById(contact.getcId()).get();
				
				if(!file.isEmpty())
					{
					//file work rewrite
					//Delete old photo
					File oldFile = new ClassPathResource("static/image").getFile();
					File deleteFile = new File(oldFile, userolddetail.getImageUrl());
					deleteFile.delete();
					
					//Upload new file
					Random random = new Random(1000);
					int randomWithNextInt = random.nextInt(9999);
				
					String n2  = String.valueOf(randomWithNextInt);				
					n2 = n2+"_"+file.getOriginalFilename();
					user.setImageUrl(n2);
					File saveFile = new ClassPathResource("static/image").getFile();
					
					Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+n2);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						
					}else
				{
					user.setImageUrl(user.getImageUrl());
					
				}
			
				//User user = this.userRepository.getUserByUserName(principal.getName());
				//contact.setUser(user);
				this.userRepository.save(user);
				session.setAttribute("message",new Message("Your Profile is updated....","alert-success"));
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
//			System.out.println("User Name : " + contact.getName());
	//		System.out.println("User ID : " + contact.getcId());
//			return "redirect:/user/"+contact.getcId()+"/contact";
			return "redirect:/user/index";
		
		}
		
		//Open Setting form Handler
		@RequestMapping("/setting")
		public String userSetting() 
		{
			return "/user/user_setting";
		}
		
		//Change password handler
		@PostMapping("/change-password")
		public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
				Principal principal,HttpSession session)
		{
			String userName = principal.getName();
			User currentUser = this.userRepository.getUserByUserName(userName);
			if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
					{
				//change password
					currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
					this.userRepository.save(currentUser);
					session.setAttribute("message",new Message( "Your password changed","alert-success"));
					}else 
					{
						//error.
						session.setAttribute("message",new Message( "Sorry! Your Old Password doesn't matched","alert-danger"));
						return "redirect:/user/setting";
					}
			return "redirect:/user/index";
		}
}


