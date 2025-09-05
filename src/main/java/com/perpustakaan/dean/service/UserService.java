package com.perpustakaan.dean.service;

import com.perpustakaan.dean.dto.ListUserDto;
import com.perpustakaan.dean.dto.UserDetailDto;

public interface UserService {
    UserDetailDto daftar(ListUserDto dto);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
