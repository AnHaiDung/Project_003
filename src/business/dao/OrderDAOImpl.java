package business.dao;

import model.entity.Order;
import model.entity.OrderDetail;
import model.entity.OrderStatus;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderDAOImpl implements IOrderDAO {
    @Override
    public int createOrder(Order order) {
        String sqlInsert = "INSERT INTO orders (user_id, table_id, order_date) VALUES (?, ?, ?)";
        String sqlGetID = "SELECT id FROM orders ORDER BY id DESC LIMIT 1";

        try (Connection conn = DBConnection.openConnection()) {
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getTableId());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(order.getOrderDate()));
            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(sqlGetID);
            var rs = ps2.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo đơn hàng");
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
            System.out.println("Không thể lưu chi tiết món ăn");
            return false;
        }
    }

    @Override
    public List<OrderDetail> getDetailsByStatus(OrderStatus status) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE status = ? ORDER BY id ASC";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new OrderDetail(rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        OrderStatus.valueOf(rs.getString("status"))));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách món.");
        }
        return list;
    }

    @Override
    public boolean updateDetailStatus(int detailId, OrderStatus newStatus) {
        String sql = "UPDATE order_details SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, detailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public List<OrderDetail> getDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new OrderDetail(rs.getInt("id"), rs.getInt("order_id"),
                        rs.getInt("item_id"), rs.getInt("quantity"),
                        OrderStatus.valueOf(rs.getString("status"))));
            }
        } catch (SQLException e) { }
        return list;
    }

    @Override
    public Order findActiveOrderByUser(int userId) {
        String sql = """
        SELECT o.* FROM orders o  
        JOIN tables t ON o.table_id = t.id 
        WHERE o.user_id = ? AND t.status = 'OCCUPIED' 
        ORDER BY o.id DESC LIMIT 1
        """;
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("table_id"),
                        rs.getTimestamp("order_date").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra đơn hàng hoạt động");
        }
        return null;
    }

    @Override
    public boolean cancelOrderDetail(int detailId, int userId) {
        String sql = """
        UPDATE order_details od
        JOIN orders o ON od.order_id = o.id
        SET od.status = 'CANCELED'
        WHERE od.id = ? AND o.user_id = ? AND od.status = 'PENDING'
        """;
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public OrderDetail findDetailById(int detailId) {
        String sql = "SELECT * FROM order_details WHERE id = ?";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new OrderDetail(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        OrderStatus.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
