package com.perpustakaan.dean.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpResetPasswordDto {
    private String username;
    private String otp;
    private String passwordBaru;
}

