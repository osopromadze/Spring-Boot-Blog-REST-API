package com.sopromadze.blogapi.utils;

import java.util.Arrays;

import com.sopromadze.blogapi.model.album.Album;
import com.sopromadze.blogapi.model.photo.Photo;
import com.sopromadze.blogapi.model.user.User;

public class TestUtils {
	
	public static Album createAlbum() {
		Album album = new Album();
		
		album.setId(Long.valueOf(1));
		album.setTitle("album");
		album.setUser(createUser());
		album.setPhoto(Arrays.asList(TestUtils.createPhoto()));
		
		return album;
	}
	
	public static User createUser() {
		User user = new User();
		
		user.setId(Long.valueOf(1));
		user.setFirstName("admin");
		user.setLastName("admin");
		user.setEmail("admin@site.com");
		user.setUsername("admin");
		user.setPassword("password");
		user.setWebsite("site.com");
		
		return user;
	}
	
	public static Photo createPhoto() {
		Photo photo = new Photo();
		
		photo.setId(Long.valueOf(1));
		
		return photo;
	}
}
