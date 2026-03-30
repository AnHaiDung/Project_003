package business.dao;

import model.entity.Order;
import model.entity.OrderDetail;
import utils.DBConnection;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class OrderDAOImpl implements IOrderDAO {
    @Override
    public int createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, table_id, order_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getTableId());
            ps.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Không thể tạo hóa đơn mới.");
        }
        return -1;
    }

    @Override
    public boolean addOrderDetails(List<OrderDetail> details) {
        String sql = "INSERT INTO order_details (order_id, item_id, quantity, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderDetail d : details) {
                ps.setInt(1, d.getOrderId());
                ps.setInt(2, d.getItemId());
                ps.setInt(3, d.getQuantity());
                ps.setString(4, d.getStatus().name());
                ps.addBatch();
            }
            return ps.executeBatch().length > 0;
        } catch (SQLException e) {
            System.out.println("Không thể lưu chi tiết món ăn.");
            return false;
        }
    }
}
