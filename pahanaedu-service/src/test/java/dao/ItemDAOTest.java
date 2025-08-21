package dao;

import dao.ItemDAO;
import models.Item;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemDAOTest {

    private static ItemDAO itemDAO;
    private static int testItemId;

    @BeforeAll
    public static void setup() {
        itemDAO = new ItemDAO();
    }

    
    
    @Test
    @Order(1)
    @DisplayName("Create a new item")
    public void testCreateItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setPrice(150.0);
        item.setQuantity(10);

        boolean created = itemDAO.createItem(item);
        testItemId = item.getId(); // store generated ID for later tests

        assertTrue(created, "Item should be created successfully");
        assertTrue(testItemId > 0, "Generated item ID should be greater than 0");
    }
    
    
    
    @Test
    @Order(2)
    @DisplayName("Get item by ID")
    public void testGetItemById() {
        Item item = itemDAO.getItemById(testItemId);

        assertNotNull(item, "Item should exist");
        assertEquals("Test Item", item.getName(), "Item name should match");
        assertEquals(150.0, item.getPrice(), "Item price should match");
        assertEquals(10, item.getQuantity(), "Item quantity should match");
    }

    
    
    
    
    @Test
    @Order(3)
    @DisplayName("Get all items")
    public void testGetItems() {
        List<Item> items = itemDAO.getItems();

        assertNotNull(items, "Item list should not be null");
        assertTrue(items.size() > 0, "Item list should contain at least one item");
    }

    
    
    
    @Test
    @Order(4)
    @DisplayName("Update item")
    public void testUpdateItem() {
        Item item = itemDAO.getItemById(testItemId);
        assertNotNull(item, "Item should exist before update");

        item.setName("Updated Item");
        item.setPrice(200.0);
        item.setQuantity(20);

        boolean updated = itemDAO.updateItem(item);
        assertTrue(updated, "Item should be updated successfully");

        Item updatedItem = itemDAO.getItemById(testItemId);
        assertEquals("Updated Item", updatedItem.getName(), "Name should be updated");
        assertEquals(200.0, updatedItem.getPrice(), "Price should be updated");
        assertEquals(20, updatedItem.getQuantity(), "Quantity should be updated");
    }

    
    
    
    
    
    @Test
    @Order(5)
    @DisplayName("Delete item")
    public void testDeleteItem() {
        boolean deleted = itemDAO.deleteItem(testItemId);
        assertTrue(deleted, "Item should be deleted successfully");

        Item item = itemDAO.getItemById(testItemId);
        assertNull(item, "Deleted item should not exist");
    }

    
    
    
    
    @Test
    @Order(6)
    @DisplayName("Handle non-existent item retrieval")
    public void testGetNonExistentItem() {
        Item item = itemDAO.getItemById(-1); // invalid ID
        assertNull(item, "Non-existent item should return null");
    }

    @Test
    @Order(7)
    @DisplayName("Handle deletion of non-existent item")
    public void testDeleteNonExistentItem() {
        boolean deleted = itemDAO.deleteItem(-1); // invalid ID
        assertFalse(deleted, "Deleting non-existent item should return false");
    }

    
}
