package utils;

import java.sql.*;

public class DBConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "06072006";

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
            return null;
        }
    }

    public static Connection openConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Chưa cài đặt Mysql Driver");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối tới database");
            e.printStackTrace();
        }
        return null;
    }

    public static void initDatabase() {
        try (
                Connection conn = openConnection();
                Statement stm = conn.createStatement()
        ) {
            if (conn == null) return;

            // 1. Bảng Người dùng
            String sqlUser = """
                create table if not exists users (
                    id int primary key auto_increment,
                    username varchar(50) unique not null,
                    password varchar(255) not null,
                    role enum('MANAGER', 'CHEF', 'CUSTOMER') not null
                )
                """;
            stm.execute(sqlUser);

            // 2. Bảng Bàn ăn
            String sqlTable = """
                create table if not exists tables (
                    id int primary key auto_increment,
                    table_number varchar(50) unique not null,
                    capacity int not null,
                    status enum('FREE', 'OCCUPIED') default 'FREE'
                )
                """;
            stm.execute(sqlTable);

            // 3. Bảng Thực đơn
            String sqlMenu = """
                create table if not exists menu_items (
                    id int primary key auto_increment,
                    name varchar(50) unique not null,
                    price double not null check(price > 0),
                    category enum('FOOD', 'DRINK') not null,
                    stock_quantity int default 0
                )
                """;
            stm.execute(sqlMenu);

            // 4. Bảng Đơn hàng
            String sqlOrder = """
                create table if not exists orders (
                    id int primary key auto_increment,
                    user_id int,
                    table_id int,
                    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (table_id) REFERENCES tables(id)
                )
                """;
            stm.execute(sqlOrder);

            // 5. Bảng Chi tiết đơn hàng
            String sqlOrderDetail = """
                create table if not exists order_details (
                    id int primary key auto_increment,
                    order_id int,
                    item_id int,
                    quantity int not null check(quantity > 0),
                    status enum('PENDING', 'COOKING', 'READY', 'SERVED') default 'PENDING',
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (item_id) REFERENCES menu_items(id)
                )
                """;
            stm.execute(sqlOrderDetail);

            // tự động tạo tài khoản của manager và chef
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                String passAdmin = PasswordHasher.hash("admin123");
                String passChef = PasswordHasher.hash("chef123");
                stm.execute("INSERT INTO users (username, password, role) VALUES ('admin', '" + passAdmin + "', 'MANAGER')");
                stm.execute("INSERT INTO users (username, password, role) VALUES ('chef', '" + passChef + "', 'CHEF')");
            }


        } catch (SQLException e) {
            System.out.println(" Lỗi khi tạo bảng: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initDatabase();
    }
}