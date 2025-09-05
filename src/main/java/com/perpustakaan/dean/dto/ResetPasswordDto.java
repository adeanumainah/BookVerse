package com.perpustakaan.dean.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResetPasswordDto {
    @NotBlank @Size(min=3,max=20)
    private String username;

    @NotBlank @Size(min=8)
    private String passwordLama;

    @NotBlank @Size(min=8)
    private String passwordBaru;
}
