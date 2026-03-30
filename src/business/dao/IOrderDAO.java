package business.dao;

import model.entity.Order;
import model.entity.OrderDetail;

import java.util.List;

public interface IOrderDAO {
    int createOrder(Order order);
    boolean addOrderDetails(List<OrderDetail> details);
}
