package com.sopromadze.blogapi.payload;

import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
	private Long id;
	private String username;
	private String firstName;
	private String lastName;
	private Instant joinedAt;
	private String email;
	private Address address;
	private String phone;
	private String website;
	private Company company;
	private Long postCount;
}
