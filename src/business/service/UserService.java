package business.service;

import business.dao.IUserDAO;
import business.dao.UserDAOImpl;
import model.entity.Users;

import java.util.Scanner;

public class UserService {
    private IUserDAO userDAO = new UserDAOImpl();

    public Users signIn(String username, String password) {
        return userDAO.login(username, password);
    }

    public boolean signUp(Users user) {
        if (user.getUsername().length() < 3){
            return false;
        }
        return userDAO.register(user);
    }
}
