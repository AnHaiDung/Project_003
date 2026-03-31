package run;

import business.service.MenuService;
import business.service.OrderService;
import business.service.TableService;
import model.entity.*;

import java.util.List;
import java.util.Scanner;

public class CustomerView {
    private final Scanner sc = new Scanner(System.in);
    private final MenuService menuService = new MenuService();
    private final TableService tableService = new TableService();
    private final OrderService orderService = new OrderService();

    public void customerMenu(Users currentUser) {
        while (true) {
            try {
                System.out.println("\n--- THỰC ĐƠN KHÁCH HÀNG ---");
                System.out.println("1. Xem thực đơn");
                System.out.println("2. Đặt bàn & Gọi món");
                System.out.println("3. hủy món");
                System.out.println("4. Đăng xuất");
                System.out.print("Chọn: ");

                String input = sc.nextLine().trim();
                if (input.isEmpty()){
                    System.out.println("Không được bỏ trống");
                    continue;
                }

                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        List<MenuItem> menu = menuService.getFullMenu();
                        if (menu.isEmpty()){
                            System.out.println("Thực đơn trống.");
                        } else{
                            menu.forEach(m -> System.out.println(m.getId() + ". " + m.getName() + " - " + m.getPrice()));
                        }
                        break;
                    case 2:
                        handleOrderFlow(currentUser);
                        break;
                    case 3:
                        handleCancelOrder(currentUser);
                        break;
                    case 4:
                        return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số");
            }
        }
    }

    private void handleOrderFlow(Users user) {
        Order activeOrder = orderService.getActiveOrder(user.getId());
        int tableId = -1;

        if (activeOrder != null) {
            tableId = activeOrder.getTableId();
            Table currentTable = tableService.getTableById(tableId);
            System.out.println("Bạn đang ngồi tại bàn: " + currentTable.getTableNumber());
        } else {
            List<Table> freeTables = tableService.getAllTables().stream()
                    .filter(t -> t.getStatus() == TableStatus.FREE).toList();

            if (freeTables.isEmpty()) {
                System.out.println("Hiện tại nhà hàng đã hết bàn trống");
                return;
            }

            System.out.println("\n--- DANH SÁCH BÀN TRỐNG ---");
            freeTables.forEach(t -> System.out.println("ID: " + t.getId() + " | Số bàn: " + t.getTableNumber()));

            while (true) {
                System.out.print("Nhập ID bàn bạn muốn ngồi: ");
                String tIdStr = sc.nextLine().trim();
                if (tIdStr.isEmpty()){
                    System.out.println("Không được để trống");
                    continue;
                }

                try {
                    int selectedId = Integer.parseInt(tIdStr);
                    if (freeTables.stream().anyMatch(t -> t.getId() == selectedId)) {
                        tableId = selectedId;
                        break;
                    } else System.out.println("ID bàn không hợp lệ");
                } catch (NumberFormatException e) {
                    System.out.println("Nhập số ID hợp lệ");
                }
            }
        }

        java.util.List<OrderDetail> cart = new java.util.ArrayList<>();
        while (true) {
            System.out.print("Nhập ID món ăn (0 để hoàn tất): ");
            String mIdStr = sc.nextLine().trim();
            if (mIdStr.isEmpty() ){
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
            if (!qStr.isEmpty()) {
                cart.add(new OrderDetail(0, 0, itemId, Integer.parseInt(qStr), OrderStatus.PENDING));
            }
        }

        if (cart.isEmpty()){
            return;
        }

        if (activeOrder != null) {
            if (orderService.addMoreItems(activeOrder.getId(), cart)) {
                System.out.println("Đã gọi thêm món");
            }
        } else {
            Order newOrder = new Order(0, user.getId(), tableId, java.time.LocalDateTime.now());
            if (orderService.placeOrder(newOrder, cart)) {
                System.out.println("Đặt bàn và gọi món thành công");
            }
        }
    }

    private void handleCancelOrder(Users user) {
        Order activeOrder = orderService.getActiveOrder(user.getId());

        if (activeOrder == null) {
            System.out.println(" Bạn hiện không ngồi bàn nào hoặc chưa gọi món");
            return;
        }

        List<OrderDetail> myPendingItems = orderService.getDetailsByOrderId(activeOrder.getId())
                .stream()
                .filter(d -> d.getStatus() == OrderStatus.PENDING)
                .toList();

        if (myPendingItems.isEmpty()) {
            System.out.println(" Không có món nào đang chờ nấu để hủy.");
            return;
        }

        System.out.println("\n--- DANH SÁCH MÓN ĐANG CHỜ ---");
        System.out.printf("%-10s | %-15s | %-5s\n", "ID ", "Món ID", "Số Lượng");
        myPendingItems.forEach(d -> System.out.printf("%-10d | %-15d | %-5d\n", d.getId(), d.getItemId(), d.getQuantity())
        );

        System.out.print("Nhập ID trong danh sách món muốn hủy: ");
        try {
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return;
            int idCancel = Integer.parseInt(input);
            if (orderService.cancelItem(idCancel, user.getId())) {
                System.out.println(" Đã hủy món thành công");
            } else {
                System.out.println("ID không đúng hoặc bếp đã bắt đầu nấu.");
            }
        } catch (NumberFormatException e) {
            System.out.println(" Vui lòng nhập ID là con số hợp lệ.");
        }
    }

}
