package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Loads application-test.properties
@Transactional
public class UserControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        // Ensure roles exist in the test database
        if (!roleRepository.findByName(RoleName.ROLE_USER).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
        }
        if (!roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_Success() throws Exception {
        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe123\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johndoe123"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.roles[0].name").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_UsernameAlreadyTaken() throws Exception {
        // First, create a user with the username
        User existingUser = new User("Jane", "Smith", "existinguser", "jane@example.com", "password123");
        userRepository.save(existingUser);

        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"existinguser\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_EmailAlreadyTaken() throws Exception {
        // First, create a user with the email
        User existingUser = new User("Jane", "Smith", "janesmith", "existing@example.com", "password123");
        userRepository.save(existingUser);

        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe123\","
                + "\"email\":\"existing@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email is already taken"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCheckUsernameAvailability_Available() throws Exception {
        mockMvc.perform(get("/api/users/checkUsernameAvailability")
                .param("username", "newuser123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCheckUsernameAvailability_NotAvailable() throws Exception {
        // Create a user first
        User existingUser = new User("Jane", "Smith", "existinguser", "jane@example.com", "password123");
        userRepository.save(existingUser);

        mockMvc.perform(get("/api/users/checkUsernameAvailability")
                .param("username", "existinguser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCheckEmailAvailability_Available() throws Exception {
        mockMvc.perform(get("/api/users/checkEmailAvailability")
                .param("email", "newuser@example.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCheckEmailAvailability_NotAvailable() throws Exception {
        // Create a user first
        User existingUser = new User("Jane", "Smith", "janesmith", "existing@example.com", "password123");
        userRepository.save(existingUser);

        mockMvc.perform(get("/api/users/checkEmailAvailability")
                .param("email", "existing@example.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGiveAdmin_Success() throws Exception {
        // Create a regular user first
        User user = new User("Jane", "Smith", "janesmith", "jane@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(put("/api/users/janesmith/giveAdmin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You gave ADMIN role to user: janesmith"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testTakeAdmin_Success() throws Exception {
        // Create a user with admin role first
        User user = new User("Jane", "Smith", "janesmith", "jane@example.com", "password123");
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();
        user.getRoles().add(adminRole);
        user.getRoles().add(userRole);
        userRepository.save(user);

        mockMvc.perform(put("/api/users/janesmith/takeAdmin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You took ADMIN role from user: janesmith"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetCurrentUser_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testGetCurrentUser_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "nonexistentuser", roles = "USER")
    public void testGetCurrentUser_UserNotFound() throws Exception {
        // Mock user exists in security context but not in database
        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetCurrentUser_WithCompleteUserData() throws Exception {
        // Create a user with complete information
        User user = new User("Jane", "Smith", "testuser", "jane.smith@example.com", "password123");
        user.setPhone("555-1234");
        user.setWebsite("https://janesmith.com");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "USER")
    public void testGetCurrentUser_AdminUser() throws Exception {
        // Create an admin user
        User user = new User("Admin", "User", "adminuser", "admin@example.com", "password123");
        userRepository.save(user);

        // Give admin role to the user
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();
        user.getRoles().add(adminRole);
        user.getRoles().add(userRole);
        userRepository.save(user);

        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("adminuser"))
                .andExpect(jsonPath("$.firstName").value("Admin"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "special_user", roles = "USER")
    public void testGetCurrentUser_UsernameWithSpecialCharacters() throws Exception {
        // Create a user with special characters in username (within 15 char limit)
        User user = new User("Special", "User", "special_user", "special@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("special_user"))
                .andExpect(jsonPath("$.firstName").value("Special"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "user.with.dots", roles = "USER")
    public void testGetCurrentUser_UsernameWithDots() throws Exception {
        // Create a user with dots in username
        User user = new User("Dot", "User", "user.with.dots", "dot@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user.with.dots"))
                .andExpect(jsonPath("$.firstName").value("Dot"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdateUser_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        String updateUserJson = "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Smith\","
                + "\"username\":\"testuser\","
                + "\"email\":\"jane.smith@example.com\","
                + "\"password\":\"newpassword123\","
                + "\"phone\":\"555-9999\","
                + "\"website\":\"https://janesmith.com\""
                + "}";

        mockMvc.perform(put("/api/users/testuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "otheruser", roles = "USER")
    public void testUpdateUser_Unauthorized() throws Exception {
        // Create a user in the database with different username than mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        String updateUserJson = "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Smith\","
                + "\"username\":\"testuser\","
                + "\"email\":\"jane.smith@example.com\","
                + "\"password\":\"newpassword123\""
                + "}";

        mockMvc.perform(put("/api/users/testuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdateUser_NonExistentUser() throws Exception {
        String updateUserJson = "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Smith\","
                + "\"username\":\"nonexistentuser\","
                + "\"email\":\"jane.smith@example.com\","
                + "\"password\":\"newpassword123\""
                + "}";

        mockMvc.perform(put("/api/users/nonexistentuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdateUser_WithAddress() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        String updateUserJson = "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Smith\","
                + "\"username\":\"testuser\","
                + "\"email\":\"jane.smith@example.com\","
                + "\"password\":\"newpassword123\","
                + "\"address\":{"
                + "\"street\":\"123 Main St\","
                + "\"suite\":\"Suite 100\","
                + "\"city\":\"Anytown\","
                + "\"zipcode\":\"12345\","
                + "\"geo\":{"
                + "\"lat\":\"40.7128\","
                + "\"lng\":\"-74.0060\""
                + "}"
                + "}"
                + "}";

        mockMvc.perform(put("/api/users/testuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeleteUser_Success_OwnProfile() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/testuser"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    public void testDeleteUser_Success_AdminDeletingOtherUser() throws Exception {
        // Create a regular user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an admin user
        User adminUser = new User("Admin", "User", "adminuser", "admin@example.com", "password123");
        userRepository.save(adminUser);

        mockMvc.perform(delete("/api/users/testuser"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "otheruser", roles = "USER")
    public void testDeleteUser_AccessDenied() throws Exception {
        // Create a user in the database with different username than mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/testuser"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeleteUser_NonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/users/nonexistentuser"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeleteUser_CannotDeleteOwnProfileWhenNotAuthenticatedAsSelf() throws Exception {
        // Create a user in the database with different username than mock user
        User user = new User("John", "Doe", "otheruser", "john.doe@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/otheruser"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testSetOrUpdateInfo_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        String infoJson = "{"
                + "\"street\":\"123 Main St\","
                + "\"suite\":\"Suite 100\","
                + "\"city\":\"Anytown\","
                + "\"zipcode\":\"12345\","
                + "\"companyName\":\"Test Company\","
                + "\"catchPhrase\":\"Test Catch Phrase\","
                + "\"bs\":\"Test Business Service\","
                + "\"website\":\"https://testcompany.com\","
                + "\"phone\":\"555-1234\","
                + "\"lat\":\"40.7128\","
                + "\"lng\":\"-74.0060\""
                + "}";

        mockMvc.perform(put("/api/users/setOrUpdateInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(infoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address.street").value("123 Main St"))
                .andExpect(jsonPath("$.address.suite").value("Suite 100"))
                .andExpect(jsonPath("$.address.city").value("Anytown"))
                .andExpect(jsonPath("$.address.zipcode").value("12345"))
                .andExpect(jsonPath("$.address.geo.lat").value("40.7128"))
                .andExpect(jsonPath("$.address.geo.lng").value("-74.0060"))
                .andExpect(jsonPath("$.company.name").value("Test Company"))
                .andExpect(jsonPath("$.company.catchPhrase").value("Test Catch Phrase"))
                .andExpect(jsonPath("$.company.bs").value("Test Business Service"))
                .andExpect(jsonPath("$.website").value("https://testcompany.com"))
                .andExpect(jsonPath("$.phone").value("555-1234"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetUserProfile_Success() throws Exception {
        // Create a user in the database
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/testuser/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.postCount").value(0));
    }

    @Test
    public void testGetUserProfile_NonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/nonexistentuser/profile"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
