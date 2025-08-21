package dao;

import dao.UserDAO;
import models.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {

    private static UserDAO userDAO;
    private static final String testUsername = "testuser123";

    @BeforeAll
    public static void setup() {
        userDAO = new UserDAO();
    }

    
    @Test
    @Order(1)
    @DisplayName("Create a new user")
    public void testCreateUser() {
        User user = new User();
        user.setUsername(testUsername);
        user.setPassword("password123");
        user.setRole("admin");

        boolean created = userDAO.createUser(user);
        assertTrue(created, "User should be created successfully");
    }

    
    
    @Test
    @Order(2)
    @DisplayName("Authenticate user")
    public void testAuthenticateUser() {
        User user = userDAO.authenticateUser(testUsername, "password123");
        assertNotNull(user, "Authentication should succeed");
        assertEquals("admin", user.getRole(), "Role should match");
    }
    
    
    @Test
    @Order(3)
    @DisplayName("Get all users")
    public void testGetAllUsers() {
        List<User> users = userDAO.getAllUsers();
        assertNotNull(users, "User list should not be null");
        assertTrue(users.size() > 0, "There should be at least one user");
    }

    
    
    @Test
    @Order(4)
    @DisplayName("Update user")
    public void testUpdateUser() {
        User user = new User();
        user.setUsername(testUsername);
        user.setPassword("newpass123");
        user.setRole("user");

        boolean updated = userDAO.updateUser(user);
        assertTrue(updated, "User should be updated successfully");

        User updatedUser = userDAO.authenticateUser(testUsername, "newpass123");
        assertNotNull(updatedUser, "Authentication with new password should succeed");
        assertEquals("user", updatedUser.getRole(), "Role should be updated");
    }
    
    
    
    @Test
    @Order(5)
    @DisplayName("Delete user")
    public void testDeleteUser() {
        boolean deleted = userDAO.deleteUser(testUsername);
        assertTrue(deleted, "User should be deleted successfully");

        User user = userDAO.authenticateUser(testUsername, "newpass123");
        assertNull(user, "Deleted user should not be found");
    }
    
    
    
    @Test
    @Order(6)
    @DisplayName("Authenticate non-existent user")
    public void testAuthenticateNonExistentUser() {
        User user = userDAO.authenticateUser("nonexistent", "nopass");
        assertNull(user, "Authentication should fail for non-existent user");
    }

    
    
    @Test
    @Order(7)
    @DisplayName("Delete non-existent user")
    public void testDeleteNonExistentUser() {
        boolean deleted = userDAO.deleteUser("nonexistent");
        assertFalse(deleted, "Deleting non-existent user should return false");
    }
    
    
}
























