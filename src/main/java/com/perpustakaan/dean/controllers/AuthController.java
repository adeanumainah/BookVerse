package com.perpustakaan.dean.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.perpustakaan.dean.dto.LoginDto;
import com.perpustakaan.dean.dto.LupaPasswordRequestDto;
import com.perpustakaan.dean.dto.OtpResetPasswordDto;
import com.perpustakaan.dean.dto.ResponUmum;
import com.perpustakaan.dean.dto.UserDetailDto;
import com.perpustakaan.dean.models.User;
import com.perpustakaan.dean.repositories.UserRepository;
import com.perpustakaan.dean.service.LoginService;
import com.perpustakaan.dean.service.LupaPasswordService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

     private final LoginService loginService;                 
    private final LupaPasswordService lupaPasswordService;   
    private final UserRepository userRepository; 

    public AuthController(LoginService loginService, UserRepository userRepository, LupaPasswordService lupaPasswordService) {
        this.loginService = loginService;
        this.lupaPasswordService = lupaPasswordService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponUmum<UserDetailDto>> login(@RequestBody LoginDto dto) {
        try {
            UserDetailDto userDetail = loginService.login(dto); 
            return ResponseEntity.ok(ResponUmum.<UserDetailDto>builder()
                    .berhasil(true)
                    .pesan("Login Telah Berhasil")
                    .data(userDetail)
                    .build());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ResponUmum.<UserDetailDto>builder()
                            .berhasil(false)
                            .pesan(ex.getReason())
                            .data(null)
                            .build());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ResponUmum.<UserDetailDto>builder()
                            .berhasil(false)
                            .pesan("Kesalahan Internal Server")
                            .data(null)
                            .build());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            userRepository.findByEmail(email).ifPresent(user -> {
                LupaPasswordRequestDto req = new LupaPasswordRequestDto();
                req.setUsername(user.getUsername()); 
                lupaPasswordService.requestPasswordReset(req);
            });

            return ResponseEntity.ok(ResponUmum.<String>builder()
                    .berhasil(true)
                    .pesan("Jika akun terdaftar, OTP telah dikirim ke email Anda.")
                    .data(null)
                    .build());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ResponUmum.<String>builder().berhasil(false).pesan(ex.getReason()).data(null).build());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(ResponUmum.<String>builder().berhasil(false).pesan("Kesalahan Internal Server").data(null).build());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp,
            @RequestParam("newPassword") String newPassword
    ) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Email tidak ditemukan"));

            OtpResetPasswordDto dto = new OtpResetPasswordDto();
            dto.setUsername(user.getUsername());
            dto.setOtp(otp);
            dto.setPasswordBaru(newPassword);

            lupaPasswordService.validasiOtpDanResetPassword(dto);

            return ResponseEntity.ok(ResponUmum.<String>builder()
                    .berhasil(true)
                    .pesan("Password berhasil direset")
                    .data(null)
                    .build());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ResponUmum.<String>builder().berhasil(false).pesan(ex.getReason()).data(null).build());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(ResponUmum.<String>builder().berhasil(false).pesan("Kesalahan Internal Server").data(null).build());
        }
    }
}
