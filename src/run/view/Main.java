package run.view;

import business.service.MenuService;
import business.service.TableService;
import business.service.UserService;
import model.entity.*;
import utils.DBConnection;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final MenuService menuService = new MenuService();
    private static final TableService tableService = new TableService();
    private static final business.service.OrderService orderService = new business.service.OrderService();

    public static void main(String[] args) {
        DBConnection.initDatabase();
        while (true) {
            System.out.println("\n--- HỆ THỐNG QUẢN LÝ NHÀ HÀNG ---");
            System.out.println("1. Đăng nhập");
            System.out.println("2. Đăng ký (Customer)");
            System.out.println("3. Thoát");
            System.out.print("Chọn: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số!");
                continue;
            }
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    System.out.println("Tạm biệt");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ");
                    break;
            }
        }
    }

    private static void handleLogin() {
        System.out.print("User: ");
        String user = sc.nextLine();
        System.out.print("Pass: ");
        String pass = sc.nextLine();
        Users users = userService.signIn(user, pass);
        if (users != null) {
            System.out.println("Chào " + users.getUsername() + " [" + users.getRole() + "]");
            if (users.getRole() == UserRole.MANAGER) {
                managerMenu();
            } else if (users.getRole() == UserRole.CHEF) {
                chefMenu();
            } else {
                customerMenu(users);
            }
        } else {
            System.out.println(" Sai tài khoản");
        }
    }

    private static void handleRegister() {
        System.out.print("Username mới: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        if (userService.signUp(new Users(0, user, pass, UserRole.CUSTOMER))) {
            System.out.println("Đăng ký thành công");
        }
    }

    // --- MENU QUẢN LÝ ---
    private static void managerMenu() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ ---");
            System.out.println("1. Quản lý Thực đơn ");
            System.out.println("2. Quản lý Bàn ăn ");
            System.out.println("3. Đăng xuất");
            System.out.print("Lựa chọn của bạn: ");

            int choice = Integer.parseInt(sc.nextLine());
            if (choice == 3) break;

            switch (choice) {
                case 1:
                    menuManagement();
                    break;
                case 2:
                    tableManagement();
                    break;
                default:
                    System.out.println("Lựa chọn không tồn tại");
                    break;
            }
        }
    }

    // --- QUẢN LÝ MÓN ĂN ---
    private static void menuManagement() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ THỰC ĐƠN ---");
            System.out.println("1. Xem danh sách món");
            System.out.println("2. Thêm món mới");
            System.out.println("3. Xóa món ăn");
            System.out.println("4. Cập nhật gia món ăn");
            System.out.println("5. Quay lại menu chính");
            System.out.print("Chọn: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    var listMenu = menuService.getFullMenu();
                    if (listMenu.isEmpty()) {
                        System.out.println(" Thực đơn đang rỗng ");
                    } else {
                        listMenu.forEach(m -> System.out.println(m.getId() + ". " + m.getName() + " - " + m.getPrice()));
                    }
                    break;
                case 2:
                    try {
                        System.out.print("Tên món: ");
                        String name = sc.nextLine().trim();
                        if (name.isEmpty()) {
                            System.out.println(" Tên món không được để trống");
                            break;
                        }

                        System.out.print("Giá: ");
                        String priceStr = sc.nextLine().trim();
                        if (priceStr.isEmpty()) {
                            System.out.println("Giá không được để trống");
                            break;
                        }
                        double price = Double.parseDouble(priceStr);

                        System.out.print("Chọn loại (1. FOOD | 2. DRINK): ");
                        String typeStr = sc.nextLine().trim();
                        if (typeStr.isEmpty()) {
                            System.out.println(" Vui lòng chọn loại món");
                            break;
                        }
                        int type = Integer.parseInt(typeStr);
                        Category cate = (type == 2) ? Category.DRINK : Category.FOOD;

                        System.out.print("Số lượng nhập kho: ");
                        String stockStr = sc.nextLine().trim();
                        if (stockStr.isEmpty()) {
                            System.out.println(" Số lượng kho không được để trống");
                            break;
                        }
                        int stock = Integer.parseInt(stockStr);

                        if (menuService.addMenu(new MenuItem(0, name, price, cate, stock))) {
                            System.out.println(" Thêm món thành công");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(" Giá và Số lượng phải là số hợp lệ");
                    }
                    break;
                case 3:
                    System.out.print("Nhập ID cần xóa: ");
                    int id = Integer.parseInt(sc.nextLine());
                    if (menuService.deleteMenu(id)) {
                        System.out.println(" Đã xóa món ăn");
                    } else {
                        System.out.println(" Không tìm thấy ID");
                    }
                    break;
                case 4:
                    System.out.print("Nhập ID món cần sửa giá: ");
                    int menuId = Integer.parseInt(sc.nextLine());
                    MenuItem itemCheck = menuService.getFullMenu().stream().filter(m -> m.getId() == menuId).findFirst().orElse(null);
                    if (itemCheck == null) {
                        System.out.println(" Không tìm thấy ID món ăn này.");
                        break;
                    }
                    System.out.print("Nhập giá mới: ");
                    double newPrice = Double.parseDouble(sc.nextLine());
                    if (menuService.updateMenuPrice(menuId, newPrice)) {
                        System.out.println(" Cập nhật giá thành công");
                    } else {
                        System.out.println("giá không hợp lệ");
                    }
                    break;
                case 5:
                    System.out.println("Thoát quản lý thực đơn");
                    return;
                default:
                    System.out.println("Không thuộc quản lý");
            }
        }
    }

    // --- QUẢN LÝ BÀN ĂN ---
    private static void tableManagement() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ BÀN ĂN ---");
            System.out.println("1. Xem danh sách bàn");
            System.out.println("2. Thêm bàn mới");
            System.out.println("3. Xóa bàn");
            System.out.println("4. Cập nhật sức chứa");
            System.out.println("5. Quay lại menu chính");
            System.out.print("Chọn: ");

            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    var listTable = tableService.getAllTables();
                    if (listTable.isEmpty()) {
                        System.out.println(" Chưa có bàn nào ");
                    } else {
                        listTable.forEach(t -> System.out.println("ID: " + t.getId() + " | Số bàn: " + t.getTableNumber() + " | Sức chứa: " + t.getCapacity() + " | Trạng thái: " + t.getStatus()));
                    }
                    break;
                case 2:
                    System.out.print("Số hiệu bàn (VD: B01): ");
                    String tNum = sc.nextLine().trim();
                    if (tNum.isEmpty()) {
                        System.out.println("Không để trống số hiệu");
                        break;
                    }

                    System.out.print("Sức chứa: ");
                    String cStr = sc.nextLine().trim();
                    if (cStr.isEmpty()) {
                        System.out.println("Không để trống sức chứa");
                        break;
                    }
                    int cap = Integer.parseInt(cStr);

                    if (tableService.createTable(new Table(0, tNum, cap, TableStatus.FREE))) {
                        System.out.println(" Thêm bàn thành công");
                    }
                    break;
                case 3:
                    System.out.print("Nhập ID bàn cần xóa: ");
                    int id = Integer.parseInt(sc.nextLine());
                    if (tableService.removeTable(id)) {
                        System.out.println("Đã xóa bàn");
                    } else {
                        System.out.println("Không tìm thấy ID bàn");
                    }
                    break;
                case 4:
                    System.out.print("Nhập ID bàn cần sửa: ");
                    int tId = Integer.parseInt(sc.nextLine());
                    System.out.print("Nhập sức chứa mới: ");
                    int newCap = Integer.parseInt(sc.nextLine());
                    if (tableService.updateTableCapacity(tId, newCap)) {
                        System.out.println(" Cập nhật sức chứa thành công");
                    } else {
                        System.out.println(" Không tìm thấy ID hoặc số lượng không hợp lệ!");
                    }
                    break;
                case 5:
                    System.out.println("Thoát quản lý bàn ăn");
                    return;
                default:
                    System.out.println("Không thuộc quản lý");

            }
        }
    }

    private static void chefMenu() {
        while (true) {
            try {
                System.out.println("\n--- HỆ THỐNG NHÀ BẾP ---");
                System.out.println("1. Xem món đang chờ (PENDING)");
                System.out.println("2. Cập nhật món đang nấu (COOKING)");
                System.out.println("3. Xác nhận món đã xong (READY)");
                System.out.println("4. Xác nhận đã phục vụ ");
                System.out.println("5. Đăng xuất");
                System.out.print("Chọn: ");

                String input = sc.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Không được để trống");
                    continue;
                }
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        var pending = orderService.getChefQueue();
                        if (pending.isEmpty()) {
                            System.out.println(" Hiện tại không có món nào đang chờ ");
                        } else {
                            pending.forEach(d -> System.out.println("ID: " + d.getId() + " | Món ID: " + d.getItemId() + " | SL: " + d.getQuantity()));
                        }
                        break;
                    case 2:
                        System.out.print("Nhập ID món muốn nấu: ");
                        String idStr2 = sc.nextLine().trim();
                        if (idStr2.isEmpty()) {
                            System.out.println("Không được để trống");
                            break;
                        }
                        int idCook = Integer.parseInt(idStr2);

                        if (orderService.updateStatus(idCook, OrderStatus.COOKING)) {
                            System.out.println("Đã chuyển sang COOKING");
                        } else {
                            System.out.println("ID món không tồn tại ");
                        }
                        break;
                    case 3:
                        System.out.print("Nhập ID dòng món đã XONG: ");
                        String idStr3 = sc.nextLine().trim();
                        if (idStr3.isEmpty()) {
                            System.out.println("Không được để trống");
                            break;
                        }
                        int idReady = Integer.parseInt(idStr3);
                        if (orderService.updateStatus(idReady, OrderStatus.READY)) {
                            System.out.println("Đã chuyển sang READY");
                        } else {
                            System.out.println("Không tìm thấy ID dòng món này");
                        }
                        break;
                    case 4:
                        System.out.print("Nhập ID dòng món đã phục vụ: ");
                        String idStr4 = sc.nextLine().trim();
                        if (idStr4.isEmpty()) {
                            System.out.println("Không đươợc để trống");
                            break;
                        }
                        int idServe = Integer.parseInt(idStr4);

                        if (orderService.serveItem(idServe)) {
                            System.out.println(" Đã phục vụ & Cập nhật kho thành công!");
                        } else {
                            System.out.println("  ID không đúng hoặc kho không đủ hàng");
                        }
                        break;
                    case 5:
                        return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ");
            }
        }
    }

    private static void customerMenu(Users currentUser) {
        while (true) {
            try {
                System.out.println("\n--- THỰC ĐƠN KHÁCH HÀNG ---");
                System.out.println("1. Xem thực đơn");
                System.out.println("2. Đặt bàn & Gọi món");
                System.out.println("3. Đăng xuất");
                System.out.print("Chọn: ");

                String input = sc.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Không được để trống");
                    continue;
                }
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        var menu = menuService.getFullMenu();
                        if (menu.isEmpty()) {
                            System.out.println("Thực đơn đang trống.");
                        } else
                            menu.forEach(m -> System.out.println(m.getId() + ". " + m.getName() + " - " + m.getPrice()));
                        break;
                    case 2:
                        handleOrderFlow(currentUser);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số");
            }
        }
    }

    private static void handleOrderFlow(Users user) {
        Order activeOrder = orderService.getActiveOrder(user.getId());
        int tableId = -1;

        if (activeOrder != null) {
            tableId = activeOrder.getTableId();
            Table currentTable = tableService.getTableById(tableId);
            System.out.println("Bạn đang ngồi tại bàn: " + currentTable.getTableNumber());
        } else {
            List<Table> freeTables = tableService.getAllTables().stream().filter(t -> t.getStatus() == TableStatus.FREE).toList();

            if (freeTables.isEmpty()) {
                System.out.println("Hiện tại nhà hàng đã hết bàn trống");
                return;
            }

            System.out.println("\n--- DANH SÁCH BÀN TRỐNG ---");
            freeTables.forEach(t -> System.out.println("ID: " + t.getId() + " | Số bàn: " + t.getTableNumber()));

            while (true) {
                System.out.print("Nhập ID bàn bạn muốn ngồi: ");
                String tIdStr = sc.nextLine().trim();
                if (tIdStr.isEmpty()) {
                    System.out.println("Không được để trống");
                    continue;
                }

                try {
                    int selectedId = Integer.parseInt(tIdStr);
                    boolean isValid = freeTables.stream().anyMatch(t -> t.getId() == selectedId);

                    if (isValid) {
                        tableId = selectedId;
                        break;
                    } else {
                        System.out.println(" ID bàn không hợp lệ hoặc bàn này đã có khách");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Vui lòng nhập số ID hợp lệ");
                }
            }
        }

        java.util.List<OrderDetail> cart = new java.util.ArrayList<>();
        while (true) {
            System.out.print("Nhập ID món ăn (Nhập 0 để hoàn tất): ");
            String mIdStr = sc.nextLine().trim();
            if (mIdStr.isEmpty()){
                System.out.println("Không được để trống");
                break;
            }
            if (mIdStr.equals("0")){
                break;
            }
            int itemId = Integer.parseInt(mIdStr);

            if (menuService.getFullMenu().stream().noneMatch(m -> m.getId() == itemId)) {
                System.out.println("ID món không tồn tại.");
                continue;
            }

            System.out.print("Số lượng: ");
            String qStr = sc.nextLine().trim();
            if (qStr.isEmpty()) continue;
            cart.add(new OrderDetail(0, 0, itemId, Integer.parseInt(qStr), OrderStatus.PENDING));
        }

        if (cart.isEmpty()) return;

        if (activeOrder != null) {
            if (orderService.addMoreItems(activeOrder.getId(), cart)) {
                System.out.println(" Đã gọi thêm món thành công");
            }
        } else {
            Order newOrder = new Order(0, user.getId(), tableId, java.time.LocalDateTime.now());
            if (orderService.placeOrder(newOrder, cart)) {
                System.out.println("Đặt bàn và gọi món thành công");
            }
        }
    }
}