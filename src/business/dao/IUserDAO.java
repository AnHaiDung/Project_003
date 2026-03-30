package business.dao;

import model.entity.Users;

public interface IUserDAO {
    boolean register(Users user);
    Users login(String username, String password);
    boolean isUsernameExist(String username);
}
