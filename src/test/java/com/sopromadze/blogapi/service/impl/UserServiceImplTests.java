package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.*;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.InfoRequest;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test(expected = BadRequestException.class)
    public void addUser_whenUsernameAlreadyTaken_thenThrowException() {
        try {
            User user = new User();
            user.setUsername("john.doe");
            Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

            userService.addUser(user);
        } catch (BadRequestException e) {
            Assert.assertFalse(e.getApiResponse().getSuccess());
            Assert.assertNotNull(e.getApiResponse().getMessage());
            throw e;
        }
    }

    @Test(expected = BadRequestException.class)
    public void addUser_whenEmailAlreadyTaken_thenThrowException() {
        try {
            User user = new User();
            user.setUsername("john.doe");
            user.setEmail("john.doe@example.com");

            Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
            Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

            userService.addUser(user);
        } catch (BadRequestException e) {
            Assert.assertFalse(e.getApiResponse().getSuccess());
            Assert.assertNotNull(e.getApiResponse().getMessage());
            throw e;
        }
    }

    @Test(expected = AppException.class)
    public void addUser_whenUserRoleNotSet_thenThrowException() {
        User user = new User();
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

        userService.addUser(user);
    }

    @Test
    public void addUser_whenUserAvailable_thenSaveUser() {
        User user = new User();
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.of(new Role()));

        userService.addUser(user);
    }

    @Test
    public void updateUser_whenUserAuthorized_thenSaveUser() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User user = new User();
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setId(1L);

        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(user);

        userService.updateUser(user, "john.doe", fakeUserPrincipal);
    }

    @Test
    public void updateUser_whenUserAdmin_thenSaveUser() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User user = new User();
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setId(2L);

        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(user);

        userService.updateUser(user, "john.doe", fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void updateUser_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User user = new User();
            user.setUsername("john.doe");
            user.setEmail("john.doe@example.com");
            user.setId(2L);

            Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(user);

            userService.updateUser(user, "john.doe", fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertFalse(e.getApiResponse().getSuccess());
            Assert.assertNotNull(e.getApiResponse().getMessage());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteUser_whenUserNotFound_thenThrowException() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        userService.deleteUser("john.doe", fakeUserPrincipal);
    }

    @Test(expected = AccessDeniedException.class)
    public void deleteUser_whenNotCurrentUserOrAdmin_thenThrowException() {
        try {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User user = new User();
            user.setId(2L);

            Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));

            userService.deleteUser("john.doe", fakeUserPrincipal);
        } catch (AccessDeniedException e) {
            Assert.assertFalse(e.getApiResponse().getSuccess());
            Assert.assertNotNull(e.getApiResponse().getMessage());
            throw e;
        }
    }

    @Test
    public void deleteUser_whenUserMatches_thenDeleteUser() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User user = new User();
        user.setId(1L);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));

        ApiResponse response = userService.deleteUser("john.doe", fakeUserPrincipal);
        Assert.assertTrue(response.getSuccess());
        Assert.assertNotNull(response.getMessage());
    }

    @Test
    public void deleteUser_whenUserAdmin_thenDeleteUser() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User user = new User();
        user.setId(2L);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));

        ApiResponse response = userService.deleteUser("john.doe", fakeUserPrincipal);
        Assert.assertTrue(response.getSuccess());
        Assert.assertNotNull(response.getMessage());
    }

    @Test(expected = AppException.class)
    public void giveAdmin_whenAdminRoleNotFound_thenThrowException() {
        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(new User());
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.empty());

        userService.giveAdmin("john.doe");
    }

    @Test(expected = AppException.class)
    public void giveAdmin_whenUserRoleNotFound_thenThrowException() {
        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(new User());
        Mockito.when(roleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(new Role()));
        Mockito.when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        userService.giveAdmin("john.doe");
    }

    @Test
    public void giveAdmin_whenUserUpdated_thenReturnSuccessResponse() {
        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(new User());
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.of(new Role()));

        ApiResponse response = userService.giveAdmin("john.doe");
        Assert.assertTrue(response.getSuccess());
        Assert.assertNotNull(response.getMessage());
    }

    @Test(expected = AppException.class)
    public void removeAdmin_whenUserRoleNotFound_thenThrowException() {
        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(new User());
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.empty());

        userService.removeAdmin("john.doe");
    }

    @Test
    public void removeAdmin_whenUserUpdated_thenReturnSuccessResponse() {
        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(new User());
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.of(new Role()));

        ApiResponse response = userService.removeAdmin("john.doe");
        Assert.assertTrue(response.getSuccess());
        Assert.assertNotNull(response.getMessage());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void setOrUpdateInfo_whenUserNotFound_thenThrowException() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        InfoRequest infoRequest = new InfoRequest();

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        userService.setOrUpdateInfo(fakeUserPrincipal, infoRequest);
    }

    @Test
    public void setOrUpdateInfo_whenUserAuthorized_thenReturnUserProfile() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setLng("-104.990250");
        infoRequest.setLat("39.739235");
        infoRequest.setStreet("1234 W Park Ln");
        infoRequest.setSuite("12345");
        infoRequest.setCity("New York");
        infoRequest.setZipcode("00000");
        infoRequest.setCompanyName("The Company Inc.");
        infoRequest.setCatchPhrase("We Are A Company");
        infoRequest.setBs("Test");
        infoRequest.setPhone("555-555-5555");
        infoRequest.setWebsite("https://example.com");
        User user = new User();
        user.setId(1L);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(postRepository.countByCreatedBy(Mockito.anyLong())).thenReturn(3L);

        userService.setOrUpdateInfo(fakeUserPrincipal, infoRequest);
    }

    @Test
    public void setOrUpdateInfo_whenUserAdmin_thenReturnUserProfile() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setLng("-104.990250");
        infoRequest.setLat("39.739235");
        infoRequest.setStreet("1234 W Park Ln");
        infoRequest.setSuite("12345");
        infoRequest.setCity("New York");
        infoRequest.setZipcode("00000");
        infoRequest.setCompanyName("The Company Inc.");
        infoRequest.setCatchPhrase("We Are A Company");
        infoRequest.setBs("Test");
        infoRequest.setPhone("555-555-5555");
        infoRequest.setWebsite("https://example.com");
        User user = new User();
        user.setId(2L);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(postRepository.countByCreatedBy(Mockito.anyLong())).thenReturn(3L);

        userService.setOrUpdateInfo(fakeUserPrincipal, infoRequest);
    }

    @Test(expected = AccessDeniedException.class)
    public void setOrUpdateInfo_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            InfoRequest infoRequest = new InfoRequest();
            infoRequest.setLng("-104.990250");
            infoRequest.setLat("39.739235");
            infoRequest.setStreet("1234 W Park Ln");
            infoRequest.setSuite("12345");
            infoRequest.setCity("New York");
            infoRequest.setZipcode("00000");
            infoRequest.setCompanyName("The Company Inc.");
            infoRequest.setCatchPhrase("We Are A Company");
            infoRequest.setBs("Test");
            infoRequest.setPhone("555-555-5555");
            infoRequest.setWebsite("https://example.com");
            User user = new User();
            user.setId(2L);

            Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
            Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
            Mockito.when(postRepository.countByCreatedBy(Mockito.anyLong())).thenReturn(3L);

            userService.setOrUpdateInfo(fakeUserPrincipal, infoRequest);
        } catch (AccessDeniedException e) {
            Assert.assertFalse(e.getApiResponse().getSuccess());
            Assert.assertNotNull(e.getApiResponse().getMessage());
            Assert.assertEquals(403, e.getApiResponse().getStatus().value());
            throw e;
        }
    }
}
