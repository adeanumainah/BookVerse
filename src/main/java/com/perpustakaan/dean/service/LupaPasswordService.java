package com.perpustakaan.dean.service;

import com.perpustakaan.dean.dto.LupaPasswordRequestDto;
import com.perpustakaan.dean.dto.ResetPasswordDto;
import com.perpustakaan.dean.dto.OtpResetPasswordDto;

public interface LupaPasswordService {
    boolean requestPasswordReset(LupaPasswordRequestDto request);
    void resetPassword(ResetPasswordDto request);
    void validasiOtpDanResetPassword(OtpResetPasswordDto dto);
}

