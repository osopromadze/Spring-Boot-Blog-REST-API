package com.sopromadze.blogapi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummary {
	private Long id;
	private String username;
	private String firstName;
	private String lastName;
}
