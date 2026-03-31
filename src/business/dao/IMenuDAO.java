package business.dao;

import model.entity.MenuItem;

import java.util.List;

public interface IMenuDAO {
    boolean add(MenuItem item);
    boolean delete(int id);
    List<MenuItem> getAll();
    MenuItem findById(int id);
    boolean updatePrice(int id, double newPrice);
    boolean updateStock(int id, int quantity);
}
