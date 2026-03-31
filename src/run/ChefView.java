package run;

import business.service.OrderService;
import model.entity.OrderDetail;
import model.entity.OrderStatus;

import java.util.List;
import java.util.Scanner;

public class ChefView {
    private final Scanner sc = new Scanner(System.in);
    private final OrderService orderService = new OrderService();

    public void chefMenu() {
        while (true) {
            try {
                System.out.println("\n--- HỆ THỐNG NHÀ BẾP ---");
                System.out.println("1. Xem món đang chờ (PENDING)");
                System.out.println("2. Cập nhật tiến độ món ");
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
                        List<OrderDetail> pending = orderService.getChefQueue();
                        if (pending.isEmpty()) {
                            System.out.println(" Hiện tại không có món nào đang chờ ");
                        } else {
                            pending.forEach(d -> System.out.println("ID: " + d.getId() + " | Món ID: " + d.getItemId() + " | Số Lượng: " + d.getQuantity()));
                        }
                        break;
                    case 2:
                        System.out.print("Nhập ID dòng món muốn cập nhật: ");
                        int id = Integer.parseInt(sc.nextLine().trim());
                        if (orderService.nextStep(id)) {
                            System.out.println(" Cập nhật thành công");
                        } else {
                            System.out.println("ID sai hoặc món đã hoàn tất");
                        }
                        break;
                    case 3:
                        return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ");
            }
        }
    }
}
