package com.sopromadze.blogapi.config;

import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

/**
 * Custom argument resolver for @CurrentUser annotation.
 * This resolver extracts the current authenticated user from the security context
 * and converts it to a UserPrincipal object for controller methods.
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    public CurrentUserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Determines if this resolver supports the given method parameter.
     * Returns true if the parameter is annotated with @CurrentUser.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    /**
     * Resolves the method parameter by extracting the current user from the security context.
     * If the user is found in the database, it creates a UserPrincipal from the User entity.
     * If not found, it returns null which will cause a NullPointerException in the controller.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            
            // Find the user in the database
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return UserPrincipal.create(user);
            }
        }
        
        return null;
    }
}