package run;

import business.service.MenuService;
import business.service.TableService;
import model.entity.*;
import java.util.List;
import java.util.Scanner;

public class ManagerView {
    private final Scanner sc = new Scanner(System.in);
    private final MenuService menuService = new MenuService();
    private final TableService tableService = new TableService();

    public void managerMenu() {
        while (true) {
            try {
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
                }
            } catch (Exception e) {
                System.out.println("Nhập số hợp lệ!");
            }
        }
    }

    // --- QUẢN LÝ MÓN ĂN ---
    private void menuManagement() {
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
    private void tableManagement() {
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
}
