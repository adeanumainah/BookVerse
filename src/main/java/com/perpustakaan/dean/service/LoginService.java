package com.perpustakaan.dean.service;

import com.perpustakaan.dean.dto.LoginDto;
import com.perpustakaan.dean.dto.UserDetailDto;

public interface LoginService {
    UserDetailDto login(LoginDto dto);
    public String loginWeb(LoginDto dto);
}
