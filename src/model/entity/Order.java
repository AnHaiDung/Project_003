package model.entity;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private int userId;
    private int tableId;
    private LocalDateTime orderDate;

    public Order() {
    }

    public Order(int id, int userId, int tableId, LocalDateTime orderDate) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.orderDate = orderDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
