package dao;

import dao.CustomerDAO;
import models.Customer;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerDAOTest {

    private static CustomerDAO customerDAO;
    private static int testCustomerId;

    @BeforeAll
    public static void setup() {
        customerDAO = new CustomerDAO();
    }

    
    @Test
    @Order(1)
    @DisplayName("Create a new customer")
    public void testCreateCustomer() {
        Customer c = new Customer();
        c.setName("Test User");
        c.setEmail("testuser@example.com");
        c.setAddress("123 Test Street");
        c.setMobile("0771234567");
        c.setUnit_consumed(100);

        boolean created = customerDAO.createCustomer(c);
        testCustomerId = c.getId(); // store generated ID for later tests

        assertTrue(created, "Customer should be created successfully");
        assertTrue(testCustomerId > 0, "Generated customer ID should be greater than 0");
    }
    
    
    
    @Test
    @Order(2)
    @DisplayName("Get customer by ID")
    public void testGetCustomerById() {
        Customer c = customerDAO.getCustomerById(testCustomerId);

        assertNotNull(c, "Customer should exist");
        assertEquals("Test User", c.getName(), "Customer name should match");
        assertEquals("testuser@example.com", c.getEmail(), "Customer email should match");
    }
    
    
    
    
    @Test
    @Order(3)
    @DisplayName("Get all customers")
    public void testGetCustomers() {
        List<Customer> customers = customerDAO.getCustomers();

        assertNotNull(customers, "Customer list should not be null");
        assertTrue(customers.size() > 0, "Customer list should contain at least one customer");
    }

    
    
    
    
    
    @Test
    @Order(4)
    @DisplayName("Update customer")
    public void testUpdateCustomer() {
        Customer c = customerDAO.getCustomerById(testCustomerId);
        assertNotNull(c, "Customer should exist before update");

        c.setName("Updated User");
        c.setUnit_consumed(200);

        boolean updated = customerDAO.updateCustomer(c);
        assertTrue(updated, "Customer should be updated successfully");

        Customer updatedCustomer = customerDAO.getCustomerById(testCustomerId);
        assertEquals("Updated User", updatedCustomer.getName(), "Name should be updated");
        assertEquals(200, updatedCustomer.getUnit_consumed(), "Units consumed should be updated");
    }

    
    
    
    
    @Test
    @Order(5)
    @DisplayName("Delete customer")
    public void testDeleteCustomer() {
        boolean deleted = customerDAO.deleteCustomer(testCustomerId);
        assertTrue(deleted, "Customer should be deleted successfully");

        Customer c = customerDAO.getCustomerById(testCustomerId);
        assertNull(c, "Deleted customer should not exist");
    }
    
    
    
    
    @Test
    @Order(6)
    @DisplayName("Handle non-existent customer retrieval")
    public void testGetNonExistentCustomer() {
        Customer c = customerDAO.getCustomerById(-1); // invalid ID
        assertNull(c, "Non-existent customer should return null");
    }

    
    
    
    @Test
    @Order(7)
    @DisplayName("Handle deletion of non-existent customer")
    public void testDeleteNonExistentCustomer() {
        boolean deleted = customerDAO.deleteCustomer(-1); // invalid ID
        assertFalse(deleted, "Deleting non-existent customer should return false");
    }
    
}
























