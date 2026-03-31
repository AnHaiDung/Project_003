package run;

import business.service.UserService;
import model.entity.UserRole;
import model.entity.Users;
import utils.DBConnection;

import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ManagerView managerView = new ManagerView();
    private static final ChefView chefView = new ChefView();
    private static final CustomerView customerView = new CustomerView();

    public static void main(String[] args) {
        DBConnection.initDatabase();
        while (true) {
            System.out.println("\n--- HỆ THỐNG QUẢN LÝ NHÀ HÀNG ---");
            System.out.println("1. Đăng nhập ");
            System.out.println("2. Đăng ký");
            System.out.println("3. Thoát");

            System.out.print("Chọn: ");
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                switch (choice) {
                    case 1:
                        handleLogin();
                        break;
                    case 2:
                        handleRegister();
                        break;
                    case 3:
                        System.exit(0);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Nhập số");
            }
        }
    }

    private static void handleLogin() {
        System.out.print("User: ");
        String user = sc.nextLine();
        System.out.print("Pass: ");
        String pass = sc.nextLine();
        Users u = userService.signIn(user, pass);
        if (u != null) {
            System.out.println("Chào " + u.getUsername());
            if (u.getRole() == UserRole.MANAGER){
                managerView.managerMenu();
            } else if (u.getRole() == UserRole.CHEF){
                chefView.chefMenu();
            } else{
                customerView.customerMenu(u);
            }
        } else{
            System.out.println("Sai tài khoản");
        }
    }

    private static void handleRegister() {
        System.out.print("User: ");
        String user = sc.nextLine();
        System.out.print("Pass: ");
        String pass = sc.nextLine();
        if (userService.signUp(new Users(0, user, pass, UserRole.CUSTOMER))){
            System.out.println("Thành công");
        }
    }
}
