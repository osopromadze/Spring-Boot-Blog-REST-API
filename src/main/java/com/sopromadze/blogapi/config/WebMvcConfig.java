package com.sopromadze.blogapi.config;

import com.sopromadze.blogapi.config.CurrentUserArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("cors.allowedOrings")
	private String allowedOrigins;

	private final CurrentUserArgumentResolver currentUserArgumentResolver;

	public WebMvcConfig(CurrentUserArgumentResolver currentUserArgumentResolver) {
		this.currentUserArgumentResolver = currentUserArgumentResolver;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		final long MAX_AGE_SECS = 3600;

		registry.addMapping("/**")
				.allowedOrigins(allowedOrigins)
				.allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowedHeaders("*")
				.maxAge(MAX_AGE_SECS);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		// Add custom argument resolvers here
		resolvers.add(currentUserArgumentResolver);
	}
}
