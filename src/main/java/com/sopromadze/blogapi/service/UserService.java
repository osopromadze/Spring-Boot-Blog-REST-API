package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.AppException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserSummary getCurrentUser(UserPrincipal currentUser) {
		return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName(),
				currentUser.getLastName());
	}

	public UserIdentityAvailability checkUsernameAvailability(String username) {
		Boolean isAvailable = !userRepository.existsByUsername(username);
		return new UserIdentityAvailability(isAvailable);
	}

	public UserIdentityAvailability checkEmailAvailability(String email) {
		Boolean isAvailable = !userRepository.existsByEmail(email);
		return new UserIdentityAvailability(isAvailable);
	}

	public UserProfile getUserProfile(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

		Long postCount = postRepository.countByCreatedBy(user.getId());

		return new UserProfile(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),
				user.getCreatedAt(), user.getEmail(), user.getAddress(), user.getPhone(), user.getWebsite(),
				user.getCompany(), postCount);
	}

	public ResponseEntity<?> addUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			return new ResponseEntity<>(new ApiResponse(false, "Username is already taken"), HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>(new ApiResponse(false, "Email is already taken"), HttpStatus.BAD_REQUEST);
		}

		List<Role> roles = new ArrayList<>();
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
		user.setRoles(roles);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User result = userRepository.save(user);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	public ResponseEntity<?> updateUser(User newUser, String username, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setFirstName(newUser.getFirstName());
			user.setLastName(newUser.getLastName());
			user.setPassword(passwordEncoder.encode(newUser.getPassword()));
			user.setAddress(newUser.getAddress());
			user.setPhone(newUser.getPhone());
			user.setWebsite(newUser.getWebsite());
			user.setCompany(newUser.getCompany());

			User updatedUser = userRepository.save(user);
			return new ResponseEntity<>(updatedUser, HttpStatus.OK);

		}

		return new ResponseEntity<>(
				new ApiResponse(false, "You don't have permission to update profile of: " + username),
				HttpStatus.UNAUTHORIZED);

	}

	public ResponseEntity<?> deleteUser(String username, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
		if (!user.getId().equals(currentUser.getId())) {
			return new ResponseEntity<>(
					new ApiResponse(false, "You don't have permission to delete profile of: " + username),
					HttpStatus.UNAUTHORIZED);
		}
		userRepository.deleteById(user.getId());

		return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted profile of: " + username),
				HttpStatus.OK);
	}

	public ResponseEntity<?> giveAdmin(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
				.orElseThrow(() -> new AppException("User role not set")));
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
		user.setRoles(roles);
		userRepository.save(user);
		return new ResponseEntity<>(new ApiResponse(true, "You gave ADMIN role to user: " + username), HttpStatus.OK);
	}

	public ResponseEntity<?> takeAdmin(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		List<Role> roles = new ArrayList<>();
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
		user.setRoles(roles);
		userRepository.save(user);
		return new ResponseEntity<>(new ApiResponse(true, "You took ADMIN role from user: " + username), HttpStatus.OK);
	}

	public ResponseEntity<?> setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest) {
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		Geo geo = new Geo(infoRequest.getLat(), infoRequest.getLng());
		Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(),
				infoRequest.getZipcode(), geo);
		Company company = new Company(infoRequest.getCompanyName(), infoRequest.getCatchPhrase(), infoRequest.getBs());
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setAddress(address);
			user.setCompany(company);
			user.setWebsite(infoRequest.getWebsite());
			user.setPhone(infoRequest.getPhone());
			User updatedUser = userRepository.save(user);

			Long postCount = postRepository.countByCreatedBy(updatedUser.getId());

			UserProfile userProfile = new UserProfile(updatedUser.getId(), updatedUser.getUsername(),
					updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getCreatedAt(),
					updatedUser.getEmail(), updatedUser.getAddress(), updatedUser.getPhone(), updatedUser.getWebsite(),
					updatedUser.getCompany(), postCount);
			return new ResponseEntity<>(userProfile, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to update users profile"),
				HttpStatus.OK);
	}
}
