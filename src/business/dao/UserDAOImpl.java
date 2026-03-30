package business.dao;

import model.entity.UserRole;
import model.entity.Users;
import utils.DBConnection;
import utils.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserDAOImpl implements IUserDAO {

    @Override
    public boolean register(Users user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordHasher.hash(user.getPassword()));
            ps.setString(3, user.getRole().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public Users login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordHasher.hash(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Users(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), UserRole.valueOf(rs.getString("role")));
            }
        } catch (SQLException e) {
            System.out.println("Quá trình đăng nhập gặp sự cố kết nối.");
        }
        return null;
    }

    @Override
    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Không thể kiểm tra trùng lặp tài khoản.");
        }
        return false;
    }
}
