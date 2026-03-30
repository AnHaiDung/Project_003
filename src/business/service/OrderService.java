package business.service;

import business.dao.IOrderDAO;
import business.dao.OrderDAOImpl;
import model.entity.Order;
import model.entity.OrderDetail;

import java.util.List;
import java.util.Scanner;

public class OrderService {
    private IOrderDAO orderDAO = new OrderDAOImpl();
    private TableService tableService = new TableService();

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
}
