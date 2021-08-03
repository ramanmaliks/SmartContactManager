package com.smart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="CONTACT")
public class Contact {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private int cId;
		@NotBlank(message="Name cannot be blank")
		@Size(min =5,max=20,message="Minimum Character 5 and Maximuman Character 20 are allowed!!")
		private  String name;
		@NotBlank(message="Last Name cannot be blank")
		private String secondName;
		@NotBlank(message="Work cannot be blank")
		private String work;
		@Pattern(regexp ="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message="Invalid Email !!")
		private String email;
		@NotBlank(message="Phone number cannot be blank")
		private String phone;
		//@NotBlank(message="Select Contact Image")
		private String image;
		@Column(length = 5000)
		@NotBlank(message="Desctiption cannot be blank")
		private String description;
		
		@ManyToOne
		@JsonIgnore
		private User user;
		
		public Contact(int cId, String name, String secondName, String work, String email, String phone, String image,
				String description, User user) {
			super();
			this.cId = cId;
			this.name = name;
			this.secondName = secondName;
			this.work = work;
			this.email = email;
			this.phone = phone;
			this.image = image;
			this.description = description;
			this.user = user;
		}

		public int getcId() {
			return cId;
		}

		public void setcId(int cId) {
			this.cId = cId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSecondName() {
			return secondName;
		}

		public void setSecondName(String secondName) {
			this.secondName = secondName;
		}

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public Contact() {
			super();
			
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return this.cId==((Contact)obj).getcId();
		}
		

//		@Override
//		public String toString() {
//			return "Contact [cId=" + cId + ", name=" + name + ", secondName=" + secondName + ", work=" + work
//					+ ", email=" + email + ", phone=" + phone + ", image=" + image + ", description=" + description
//					+ ", user=" + user + "]";
//		}


}

