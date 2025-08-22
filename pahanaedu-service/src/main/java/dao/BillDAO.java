package dao;

import models.Bill;
import models.BillItem;
import models.Customer;
import models.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public boolean createBill(Bill bill) {
        String insertBillSQL = "INSERT INTO bills (customer_id, bill_date, total_amount) VALUES (?, ?, ?)";
        String insertBillItemSQL = "INSERT INTO bill_items (bill_id, item_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
        String updateItemStockSQL = "UPDATE items SET quantity = quantity - ? WHERE id = ?";
        String updateCustomerUnitsSQL = "UPDATE customers SET unit_consumed = unit_consumed + ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            if (bill.getCustomer() == null || bill.getCustomer().getId() <= 0) {
                return false;
            }

            // Insert Bill
            try (PreparedStatement billStmt = conn.prepareStatement(insertBillSQL, Statement.RETURN_GENERATED_KEYS)) {
                billStmt.setInt(1, bill.getCustomer().getId());
                billStmt.setTimestamp(2, new Timestamp(bill.getBillDate().getTime()));
                billStmt.setDouble(3, bill.getTotalAmount());

                if (billStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = billStmt.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        conn.rollback();
                        return false;
                    }
                    bill.setId(generatedKeys.getInt(1));
                }
            }

            try (PreparedStatement billItemStmt = conn.prepareStatement(insertBillItemSQL);
                 PreparedStatement updateStockStmt = conn.prepareStatement(updateItemStockSQL)) {

                int totalUnitsSold = 0;
                for (BillItem item : bill.getBillItems()) {
                    billItemStmt.setInt(1, bill.getId());
                    billItemStmt.setInt(2, item.getItem().getId());
                    billItemStmt.setInt(3, item.getQuantity());
                    billItemStmt.setDouble(4, item.getPrice());
                    billItemStmt.setDouble(5, item.getSubtotal());
                    billItemStmt.addBatch();

                    updateStockStmt.setInt(1, item.getQuantity());
                    updateStockStmt.setInt(2, item.getItem().getId());
                    updateStockStmt.addBatch();

                    totalUnitsSold += item.getQuantity();
                }

                billItemStmt.executeBatch();
                updateStockStmt.executeBatch();

                try (PreparedStatement updateCustomerStmt = conn.prepareStatement(updateCustomerUnitsSQL)) {
                    updateCustomerStmt.setInt(1, totalUnitsSold);
                    updateCustomerStmt.setInt(2, bill.getCustomer().getId());
                    updateCustomerStmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

public int getLastBillId() {
    int lastId = 0; // default to 0 if no rows
    String sql = "SELECT id FROM bills ORDER BY id DESC LIMIT 1"; // last inserted bill

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) { // if a row exists
            lastId = rs.getInt("id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lastId;
}

    public List<Bill> getBills() {
        List<Bill> bills = new ArrayList<>();
        String sqlBills = "SELECT b.id, b.bill_date, b.total_amount, " +
                "c.id as cust_id, c.name as cust_name, c.email, c.address, c.mobile " +
                "FROM bills b JOIN customers c ON b.customer_id = c.id ORDER BY b.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBills);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillDate(rs.getTimestamp("bill_date"));
                bill.setTotalAmount(rs.getDouble("total_amount"));

                Customer customer = new Customer();
                customer.setId(rs.getInt("cust_id"));
                customer.setName(rs.getString("cust_name"));
                customer.setEmail(rs.getString("email"));
                customer.setAddress(rs.getString("address"));
                customer.setMobile(rs.getString("mobile"));
                bill.setCustomer(customer);

                bill.setBillItems(getBillItemsByBillId(bill.getId()));
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public List<BillItem> getBillItemsByBillId(int billId) {
        List<BillItem> billItems = new ArrayList<>();
        String sqlBillItems = "SELECT bi.id, bi.quantity, bi.price, bi.subtotal, " +
                "i.id as item_id, i.name as item_name, i.price as item_price " +
                "FROM bill_items bi JOIN items i ON bi.item_id = i.id WHERE bi.bill_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBillItems)) {

            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                BillItem bi = new BillItem();
                bi.setId(rs.getInt("id"));
                bi.setQuantity(rs.getInt("quantity"));
                bi.setPrice(rs.getDouble("price"));
                bi.setSubtotal(rs.getDouble("subtotal"));

                Item item = new Item();
                item.setId(rs.getInt("item_id"));
                item.setName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("item_price"));
                bi.setItem(item);

                billItems.add(bi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return billItems;
    }

    public Bill getBillById(int id) {
        Bill bill = null;
        String sqlBill = "SELECT b.id, b.bill_date, b.total_amount, " +
                "c.id as customer_id, c.name, c.email, c.address, c.mobile " +
                "FROM bills b JOIN customers c ON b.customer_id = c.id WHERE b.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBill)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillDate(rs.getTimestamp("bill_date"));
                bill.setTotalAmount(rs.getDouble("total_amount"));

                Customer customer = new Customer();
                customer.setId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setAddress(rs.getString("address"));
                customer.setMobile(rs.getString("mobile"));
                bill.setCustomer(customer);

                bill.setBillItems(getBillItemsByBillId(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bill;
    }
     public boolean deleteBillItemsByBillId(int billId) {
        String deleteBillItemsSQL = "DELETE FROM bill_items WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteBillItemsSQL)) {
            stmt.setInt(1, billId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBill(int billId) {
        String deleteBillSQL = "DELETE FROM bills WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Delete bill items first (due to FK constraints)
            if (!deleteBillItemsByBillId(billId)) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteBillSQL)) {
                stmt.setInt(1, billId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
