package business.service;

import business.dao.IMenuDAO;
import business.dao.MenuDAOImpl;
import model.entity.MenuItem;

import java.util.List;
import java.util.Scanner;

public class MenuService {
    private IMenuDAO menuDAO = new MenuDAOImpl();

    public List<MenuItem> getFullMenu() {
        return menuDAO.getAll();
    }

    public boolean addMenu(MenuItem item) {
        if (item.getPrice() <= 0) {
            System.out.println("Giá phải lớn hơn 0");
            return false;
        }
        return menuDAO.add(item);
    }

    public boolean updateMenuPrice(int id, double price) {
        if (price <= 0){
            return false;
        }
        return menuDAO.updatePrice(id, price);
    }

    public boolean deleteMenu(int id) {
        return menuDAO.delete(id);
    }
}
