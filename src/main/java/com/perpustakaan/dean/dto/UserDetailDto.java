package com.perpustakaan.dean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailDto {
    private String username;
    private String role;
    private String namaLengkap;
    private String aksesToken;
}
