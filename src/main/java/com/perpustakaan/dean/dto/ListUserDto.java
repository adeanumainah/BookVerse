package com.perpustakaan.dean.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListUserDto {
    @NotBlank(message = "Username harus diisi")
    @Size(min = 3, max = 20, message = "Username 3–20 karakter")
    @Pattern(regexp = "^[\\w.@-]+$", message = "Username tidak boleh mengandung spasi atau karakter aneh")
    private String username;

    @NotBlank(message = "Password harus diisi")
    private String password;

    @NotBlank(message = "Email harus diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nama lengkap harus diisi")
    @Size(min = 3, max = 50, message = "Nama lengkap minimal 3 karakter")
    private String namaLengkap; 
}

