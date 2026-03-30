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
            System.out.println(" Tên đăng nhập phải có ít nhất 3 ký tự");
            return false;
        }
        if (userDAO.isUsernameExist(user.getUsername())) {
            System.out.println("Tên đăng nhập đã tồn tại");
            return false;
        }
        return userDAO.register(user);
    }
}
