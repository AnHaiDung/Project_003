package business.service;

import business.dao.IOrderDAO;
import business.dao.OrderDAOImpl;
import model.entity.Order;
import model.entity.OrderDetail;
import model.entity.OrderStatus;

import java.util.List;
import java.util.Scanner;

public class OrderService {
    private IOrderDAO orderDAO = new OrderDAOImpl();
    private TableService tableService = new TableService();
    private business.dao.IMenuDAO menuDAO = new business.dao.MenuDAOImpl();

    public boolean placeOrder(Order order, List<OrderDetail> details) {
        int orderId = orderDAO.createOrder(order);
        if (orderId == -1){
            return false;
        }

        for (OrderDetail d : details) {
            d.setOrderId(orderId);
        }

        boolean detailsSuccess = orderDAO.addOrderDetails(details);

        if (detailsSuccess) {
            return tableService.updateTableStatus(order.getTableId(), "OCCUPIED");
        }
        return false;
    }

    public List<OrderDetail> getChefQueue() {
        return orderDAO.getDetailsByStatus(OrderStatus.PENDING);
    }

    public List<OrderDetail> getCookingQueue() {
        return orderDAO.getDetailsByStatus(OrderStatus.COOKING);
    }

    public boolean updateStatus(int detailId, OrderStatus status) {
        boolean success = orderDAO.updateDetailStatus(detailId, status);
        if (success && status == OrderStatus.COOKING) {
            List<OrderDetail> cookingItems = orderDAO.getDetailsByStatus(OrderStatus.COOKING);
            OrderDetail detail = cookingItems.stream()
                    .filter(d -> d.getId() == detailId)
                    .findFirst()
                    .orElse(null);

            if (detail != null) {
                menuDAO.updateStock(detail.getItemId(), detail.getQuantity());
            }
        }
        return success;
    }

    public List<OrderDetail> getCustomerOrders(int userId) {
        return orderDAO.getDetailsByStatus(OrderStatus.PENDING);
    }

    public boolean serveItem(int detailId) {
        return orderDAO.updateDetailStatus(detailId, model.entity.OrderStatus.SERVED);
    }

    public Order getActiveOrder(int userId) {
        return orderDAO.findActiveOrderByUser(userId);
    }


    public boolean addMoreItems(int orderId, List<OrderDetail> details) {
        for (OrderDetail d : details) {
            d.setOrderId(orderId);
        }
        return orderDAO.addOrderDetails(details);
    }

    public boolean cancelItem(int detailId, int userId) {
        return orderDAO.cancelOrderDetail(detailId, userId);
    }

    public List<OrderDetail> getDetailsByOrderId(int orderId) {
        return orderDAO.getDetailsByOrderId(orderId);
    }
}
