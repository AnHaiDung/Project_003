package business.service;

import business.dao.ITableDAO;
import business.dao.TableDAOImpl;
import model.entity.Table;

import java.util.List;
import java.util.Scanner;

public class TableService {
    private ITableDAO tableDAO = new TableDAOImpl();

    public List<Table> getAllTables() {
        return tableDAO.getAll();
    }

    public boolean createTable(Table table) {
        if (table.getCapacity() < 1) {
            System.err.println("Sức chứa của bàn phải ít nhất là 1 người");
            return false;
        }
        return tableDAO.add(table);
    }

    public boolean updateTableCapacity(int id, int capacity) {
        if (capacity < 1){
            return false;
        }
        return tableDAO.updateCapacity(id, capacity);
    }

    public boolean removeTable(int id) {
        return tableDAO.delete(id);
    }

    public Table getTableById(int id) {
        return tableDAO.findById(id);
    }

    public boolean updateTableStatus(int id, String status) {
        return tableDAO.updateStatus(id, status);
    }
}
