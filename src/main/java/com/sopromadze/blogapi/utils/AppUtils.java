package com.sopromadze.blogapi.utils;

import com.sopromadze.blogapi.exception.BlogapiException;
import org.springframework.http.HttpStatus;

public class AppUtils {
	public static void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BlogapiException(HttpStatus.BAD_REQUEST, "Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new BlogapiException(HttpStatus.BAD_REQUEST, "Size number cannot be less than zero.");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new BlogapiException(HttpStatus.BAD_REQUEST, "Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
		}
	}
}
