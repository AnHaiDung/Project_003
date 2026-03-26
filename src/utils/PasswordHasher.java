package utils;

import java.util.Scanner;

public class PasswordHasher {
    // Biến mật khẩu thành một chuỗi mã hóa đơn giản
    public static String hashPassword(String password) {
        if (password == null){
            return null;
        }
        return String.valueOf((password + "RESTAURANT").hashCode());
    }
}
