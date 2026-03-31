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
                        List<OrderDetail> pending = orderService.getChefQueue();
                        if (pending.isEmpty()) {
                            System.out.println(" Hiện tại không có món nào đang chờ ");
                        } else {
                            pending.forEach(d -> System.out.println("ID: " + d.getId() + " | Món ID: " + d.getItemId() + " | Số Lượng: " + d.getQuantity()));
                        }
                        break;
                    case 2:
                        System.out.print("Nhập ID món muốn nấu: ");
                        String idStr2 = sc.nextLine().trim();
                        if (!idStr2.isEmpty()) {
                            int id = Integer.parseInt(idStr2);
                            if (checkCurrentStatus(id, OrderStatus.PENDING)) {
                                if (orderService.updateStatus(id, OrderStatus.COOKING)) {
                                    System.out.println("Đã chuyển sang COOKING và trừ kho thành công.");
                                } else {
                                    System.out.println("Lỗi khi cập nhật trạng thái hoặc trừ kho.");
                                }
                            } else {
                                System.out.println("Món này không ở trạng thái PENDING hoặc không tồn tại.");
                            }
                        }else {
                            System.out.println("Không được để trống");
                        }
                        break;
                    case 3:
                        System.out.print("Nhập ID dòng món đã XONG: ");
                        String idStr3 = sc.nextLine().trim();
                        if (!idStr3.isEmpty()) {
                            int id = Integer.parseInt(idStr3);
                            if (checkCurrentStatus(id, OrderStatus.COOKING)) {
                                if (orderService.updateStatus(id, OrderStatus.READY)) {
                                    System.out.println("Đã chuyển sang READY.");
                                }
                            } else {
                                System.out.println("Món này chưa được nấu hoặc không tồn tại.");
                            }
                        }else {
                            System.out.println("Không được để trống");
                        }
                        break;
                    case 4:
                        System.out.print("Nhập ID dòng món đã phục vụ: ");
                        String idStr4 = sc.nextLine().trim();
                        if (!idStr4.isEmpty()) {
                            int id = Integer.parseInt(idStr4);
                            if (checkCurrentStatus(id, OrderStatus.READY)) {
                                if (orderService.serveItem(id)) {
                                    System.out.println(" Đã phục vụ ");
                                } else {
                                    System.out.println("Lỗi hệ thống khi cập nhật kho.");
                                }
                            } else {
                                System.out.println(" Món này chưa sẵn sàng hoặc không tồn tại.");
                            }
                        }else {
                            System.out.println("Khng được để trống");
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
    private boolean checkCurrentStatus(int detailId, OrderStatus requiredStatus) {
        List<OrderDetail> allDetails = orderService.getChefQueue();
        allDetails.addAll(orderService.getCookingQueue());
        return allDetails.stream().anyMatch(d -> d.getId() == detailId && d.getStatus() == requiredStatus);
    }
}
