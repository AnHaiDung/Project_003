package business.dao;

import model.entity.Table;

import java.util.List;

public interface ITableDAO {
    boolean add(Table table);
    boolean updateStatus(int id, String status);
    boolean delete(int id);
    List<Table> getAll();
    Table findById(int id);
    boolean updateCapacity(int id, int newCapacity);
}
