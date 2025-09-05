package com.perpustakaan.dean.service;

import com.perpustakaan.dean.dto.LupaPasswordRequestDto;
import com.perpustakaan.dean.dto.OtpResetPasswordDto;
import com.perpustakaan.dean.dto.ResetPasswordDto;
import com.perpustakaan.dean.models.TokenResetPassword;
import com.perpustakaan.dean.models.User;
import com.perpustakaan.dean.repositories.TokenResetPasswordRepository;
import com.perpustakaan.dean.repositories.UserRepository;
import com.perpustakaan.dean.util.OtpUtil;
import com.perpustakaan.dean.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class LupaPasswordServiceImpl implements LupaPasswordService {

    private final UserRepository userRepository;
    private final TokenResetPasswordRepository tokenResetPasswordRepository;
    private final EmailService emailService;

    private static final int OTP_TTL_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 5;

    @Override
    public boolean requestPasswordReset(LupaPasswordRequestDto request) {
        final String username = request.getUsername();
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua field harus diisi");
        }
        final String trimmedUsername = username.trim();

        userRepository.findByUsername(trimmedUsername).ifPresent(user -> {
            tokenResetPasswordRepository.findByUsername(trimmedUsername).ifPresent(existing -> {
                if (existing.getTanggalKadaluarsa().isAfter(LocalDateTime.now())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "OTP masih berlaku, silakan cek email Anda");
                } else {
                    tokenResetPasswordRepository.deleteByUsername(trimmedUsername);
                }
            });

            String otp = generateSecureOtp();
            String hashedOtp = OtpUtil.hashOtp(otp);

            TokenResetPassword token = TokenResetPassword.builder()
                    .username(trimmedUsername)
                    .token(hashedOtp)
                    .tanggalKadaluarsa(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES))
                    .attempts(0)
                    .build();

            tokenResetPasswordRepository.save(token);

            ZonedDateTime zdt = token.getTanggalKadaluarsa().atZone(ZoneId.of("Asia/Jakarta"));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm 'WIB'", new Locale("id", "ID"));
            String expiredStr = zdt.format(fmt);

            emailService.kirimOtpEmail(user.getEmail(), "OTP Reset Password", otp, expiredStr, username);
        });

        return true;

    }

    private String generateSecureOtp() {
        SecureRandom sr = new SecureRandom();
        int n = sr.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    @Override
    public void resetPassword(ResetPasswordDto request) {
        String username = request.getUsername();
        if (username == null || username.isBlank()
                || request.getPasswordLama() == null || request.getPasswordLama().isBlank()
                || request.getPasswordBaru() == null || request.getPasswordBaru().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua Field Harus Diisi");
        }
        username = username.trim();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau password salah"));

        if (!PasswordUtil.check(request.getPasswordLama(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau password salah");
        }

        if (PasswordUtil.check(request.getPasswordBaru(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password baru tidak boleh sama dengan password lama");
        }

        if (!PasswordUtil.validasiPassword(user.getUsername(), request.getPasswordBaru())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password tidak memenuhi kriteria: minimal 8 karakter, kombinasi huruf, angka, karakter khusus, tanpa spasi");
        }

        user.setPassword(PasswordUtil.hash(request.getPasswordBaru()));
        userRepository.save(user);

        tokenResetPasswordRepository.deleteByUsername(username);
    }

    @Override
    public void validasiOtpDanResetPassword(OtpResetPasswordDto dto) {
        String username = dto.getUsername();
        if (username == null || username.isBlank()
                || dto.getOtp() == null || dto.getOtp().isBlank()
                || dto.getPasswordBaru() == null || dto.getPasswordBaru().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua Field Harus Diisi");
        }
        username = username.trim();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP tidak valid"));

        TokenResetPassword token = tokenResetPasswordRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP tidak valid"));

        if (token.getTanggalKadaluarsa().isBefore(LocalDateTime.now())) {
            tokenResetPasswordRepository.deleteByUsername(username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP sudah kadaluarsa");
        }

        if (token.getAttempts() != null && token.getAttempts() >= MAX_OTP_ATTEMPTS) {
            tokenResetPasswordRepository.deleteByUsername(username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Percobaan OTP melebihi batas. Minta OTP baru.");
        }

        boolean ok = OtpUtil.checkOtp(dto.getOtp(), token.getToken());
        if (!ok) {
            token.setAttempts(token.getAttempts() + 1);
            tokenResetPasswordRepository.save(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP yang Anda masukkan tidak valid");
        }

        if (PasswordUtil.check(dto.getPasswordBaru(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password baru tidak boleh sama dengan password lama");
        }
        if (!PasswordUtil.validasiPassword(user.getUsername(), dto.getPasswordBaru())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password tidak memenuhi kriteria: minimal 8 karakter, kombinasi huruf, angka, karakter khusus, tanpa spasi");
        }

        user.setPassword(PasswordUtil.hash(dto.getPasswordBaru()));
        userRepository.save(user);
        tokenResetPasswordRepository.deleteByUsername(username);
    }
}
