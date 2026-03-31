package business.dao;

import model.entity.Order;
import model.entity.OrderDetail;
import model.entity.OrderStatus;

import java.util.List;

public interface IOrderDAO {
    int createOrder(Order order);
    boolean addOrderDetails(List<OrderDetail> details);
    List<OrderDetail> getDetailsByStatus(OrderStatus status);
    List<OrderDetail> getDetailsByOrderId(int orderId);
    boolean updateDetailStatus(int detailId, OrderStatus newStatus);
    Order findActiveOrderByUser(int userId);
    boolean cancelOrderDetail(int detailId, int userId);
    OrderDetail findDetailById(int detailId);
}
