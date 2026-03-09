package com.sopromadze.blogapi.config;

import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

	@Bean
	public AuditorAware<Long> auditorProvider() {
		return new SpringSecurityAuditAwareImpl();
	}
}

class SpringSecurityAuditAwareImpl implements AuditorAware<Long> {

	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();
		
		// Handle both UserPrincipal and standard Spring Security User objects
		if (principal instanceof UserPrincipal) {
			return Optional.ofNullable(((UserPrincipal) principal).getId());
		} else if (principal instanceof org.springframework.security.core.userdetails.User) {
			// For @WithMockUser, return null to skip auditing
			return Optional.empty();
		}

		return Optional.empty();
	}
}
