package com.sopromadze.blogapi.service;

import org.springframework.http.ResponseEntity;

import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.InfoRequest;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.payload.UserSummary;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface UserService {

	UserSummary getCurrentUser(UserPrincipal currentUser);

	UserIdentityAvailability checkUsernameAvailability(String username);

	UserIdentityAvailability checkEmailAvailability(String email);

	UserProfile getUserProfile(String username);

	ResponseEntity<?> addUser(User user);

	ResponseEntity<?> updateUser(User newUser, String username, UserPrincipal currentUser);

	ResponseEntity<?> deleteUser(String username, UserPrincipal currentUser);

	ResponseEntity<?> giveAdmin(String username);

	ResponseEntity<?> takeAdmin(String username);

	ResponseEntity<?> setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest);

}