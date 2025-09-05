package com.perpustakaan.dean.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean check(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    public static boolean validasiPassword(String username, String password) {
        if (password == null || username == null)
            return false;

        if (password.equalsIgnoreCase(username)) {
            return false;
        }

        if (password.length() < 8  || password.length() > 20) {
            return false;
        }

        if (password.contains(" ")) {
            return false;
        }

        String regexHuruf = ".*[a-zA-Z].*";
        String regexAngka = ".*[0-9].*";
        String regexKarakterAcak = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";

        return password.matches(regexHuruf)
                && password.matches(regexAngka)
                && password.matches(regexKarakterAcak);
    }
}
