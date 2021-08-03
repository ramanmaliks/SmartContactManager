package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="user")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@NotBlank(message="Name cannot be blank")
	@Size(min =5,max=20,message="Minimum Character 5 and Maximuman Character 20 are allowed!!")
	private String name;
	@Column(unique = true)
	@Pattern(regexp ="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message="Invalid Email !!")
	private String email;
	@NotBlank(message="Password cannot be blank")
	private String password;
	@Column(length=500)
	@NotBlank(message="About cannot be blank")
	private String about;
	private String Role;
	@AssertTrue(message="Accept Terms and Condition")
	private boolean enabled;
	private String imageUrl;

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Contact> contacts =new ArrayList<>();
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getRole() {
		return Role;
	}
	public void setRole(String role) {
		this.Role = role;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}
	
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	
	public User() {
		super();
		
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", about=" + about
				+ ", Role=" + Role + ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", contacts=" + contacts + "]";
	}
	public User(int id,
			@NotBlank(message = "Name cannot be blank") @Size(min = 5, max = 20, message = "Minimum Character 5 and Maximuman Character 20 are allowed!!") String name,
			@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid Email !!") String email,
			@NotBlank(message = "Password cannot be blank") String password,
			@NotBlank(message = "About cannot be blank") String about, String role,
			@AssertTrue(message = "Accept Terms and Condition") boolean enabled, String imageUrl,
			List<Contact> contacts) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.about = about;
		Role = role;
		this.enabled = enabled;
		this.imageUrl = imageUrl;
		this.contacts = contacts;
	}

	
	
	
	
}
