package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
public class CustomUserDetailsServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsServiceImpl customUserDetailsService;

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_whenUserNotFound_theThrowException() {
        Mockito.when(userRepository.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        customUserDetailsService.loadUserByUsername("john.doe@example.com");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserById_whenUserNotFound_thenThrowException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        customUserDetailsService.loadUserById(1L);
    }
}
