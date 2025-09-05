package com.perpustakaan.dean.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class LupaPasswordRequestDto {

    @NotBlank(message = "Username tidak boleh kosong")
    private String username;
}

