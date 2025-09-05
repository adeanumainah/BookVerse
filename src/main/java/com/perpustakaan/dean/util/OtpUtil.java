package com.perpustakaan.dean.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class OtpUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashOtp(String otp) {
        String hashed = encoder.encode(otp);
        return hashed;
    }

    public static boolean checkOtp(String rawOtp, String hashedOtp) {
        boolean matches = encoder.matches(rawOtp, hashedOtp);
        return matches;
    }
}
