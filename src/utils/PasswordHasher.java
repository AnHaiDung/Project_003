package utils;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class PasswordHasher {
    // Biến mật khẩu thành một chuỗi mã hóa đơn giản
    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
