package dao;

import models.Bill;
import models.BillItem;
import models.Customer;
import models.Item;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillDAOTest {

    private BillDAO billDAO;
    private Customer testCustomer;
    private Item testItem;
    private Bill testBill;

    private MockedStatic<DBConnection> dbConnectionMock;

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws Exception {
        billDAO = new BillDAO();

        // Mock DBConnection.getConnection()
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        if (dbConnectionMock != null) {
            dbConnectionMock.close(); // deregister any previous static mock
        }
        dbConnectionMock = mockStatic(DBConnection.class);
        dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);

        // Common mock behaviors
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);

        // Default ResultSet behavior for single row
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Prepare test data
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");

        testItem = new Item();
        testItem.setId(1);
        testItem.setName("Test Item");
        testItem.setPrice(10.0);
        testItem.setQuantity(100);

        BillItem billItem = new BillItem();
        billItem.setItem(testItem);
        billItem.setQuantity(2);
        billItem.setPrice(testItem.getPrice());
        billItem.setSubtotal(20.0);

        testBill = new Bill();
        testBill.setId(1);
        testBill.setCustomer(testCustomer);
        testBill.setBillDate(new java.sql.Date(System.currentTimeMillis()));
        testBill.setTotalAmount(20.0);
        testBill.getBillItems().add(billItem);
    }

    @AfterEach
    public void tearDown() {
        dbConnectionMock.close();
    }

    
    @Test
    public void testCreateBill() throws Exception {
        boolean created = billDAO.createBill(testBill);
        assertTrue(created);
        assertEquals(1, testBill.getId());
    }
    
    
    
    
    @Test
    public void testGetLastBillId() throws Exception {
    // Make sure executeQuery returns mockResultSet
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Mock ResultSet for exactly one row
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("id")).thenReturn(1); // use column name

    int lastId = billDAO.getLastBillId();
    assertEquals(1, lastId); // now this will pass
    }
    
    
    
    
    @Test
    public void testGetBillById() throws Exception {
        reset(mockPreparedStatement, mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(testBill.getId());
        when(mockResultSet.getDouble("total_amount")).thenReturn(testBill.getTotalAmount());
        when(mockResultSet.getDate("bill_date")).thenReturn(new java.sql.Date(testBill.getBillDate().getTime()));
        when(mockResultSet.getInt("customer_id")).thenReturn(testCustomer.getId());

        Bill bill = billDAO.getBillById(1);
        assertNotNull(bill);
        assertEquals(testBill.getId(), bill.getId());
        assertEquals(testBill.getTotalAmount(), bill.getTotalAmount(), 0.001);
    }

    @Test
    public void testGetBillByIdNotFound() throws Exception {
        reset(mockPreparedStatement, mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Bill bill = billDAO.getBillById(999);
        assertNull(bill);
    }

    
    
    
    
    @Test
    public void testGetBillItemsByBillId() throws Exception {
        reset(mockPreparedStatement, mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("item_id")).thenReturn(testItem.getId());
        when(mockResultSet.getInt("quantity")).thenReturn(2);
        when(mockResultSet.getDouble("price")).thenReturn(testItem.getPrice());
        when(mockResultSet.getDouble("subtotal")).thenReturn(20.0);

        List<BillItem> items = billDAO.getBillItemsByBillId(1);
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(testItem.getId(), items.get(0).getItem().getId());
    }

    
    
    
    
    @Test
    public void testGetBills() throws Exception {
        reset(mockPreparedStatement, mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(testBill.getId());
        when(mockResultSet.getDouble("total_amount")).thenReturn(testBill.getTotalAmount());
        when(mockResultSet.getDate("bill_date")).thenReturn(new java.sql.Date(testBill.getBillDate().getTime()));
        when(mockResultSet.getInt("customer_id")).thenReturn(testCustomer.getId());

        List<Bill> bills = billDAO.getBills();
        assertNotNull(bills);
        assertEquals(1, bills.size());
        assertEquals(testBill.getId(), bills.get(0).getId());
    }
    
}
