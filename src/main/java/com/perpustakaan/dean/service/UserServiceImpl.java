package com.perpustakaan.dean.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.perpustakaan.dean.dto.ListUserDto;
import com.perpustakaan.dean.dto.UserDetailDto;
import com.perpustakaan.dean.models.Role;
import com.perpustakaan.dean.models.User;
import com.perpustakaan.dean.repositories.RoleRepository;
import com.perpustakaan.dean.repositories.UserRepository;
import com.perpustakaan.dean.util.PasswordUtil;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public UserDetailDto daftar(ListUserDto dto) {

        if (dto.getUsername() == null || dto.getUsername().trim().isBlank()
                || dto.getPassword() == null || dto.getPassword().trim().isBlank()
                || dto.getEmail() == null || dto.getEmail().trim().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua Field Harus Diisi");
        }

        if (dto.getUsername().length() < 3 || dto.getUsername().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username Minimal 3 Karakter Dan Maksimal 20 Karakter");
        }

        if (!emailService.cekValidEmail(dto.getEmail())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Format Email Tidak Valid");
        }

        if (!dto.getUsername().matches("^[\\w.@-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username Tidak Boleh Mengandung Spasi Atau Karakter Aneh");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username Yang Anda Buat Sudah Digunakan");
        }
        if (!PasswordUtil.validasiPassword(dto.getUsername(), dto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password Minimal 8 Karakter, Tidak Ada Spasi, Dan Harus Mengandung Huruf, Angka, Serta Karakter Khusus");
        }

        if (!dto.getNamaLengkap().matches("^[a-zA-Z]+(\\s[a-zA-Z]+)*$")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Nama Lengkap Hanya Boleh Berisi Huruf Dan Spasi(Tidak Ada Spasi DiAwal dan Diakhir)");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah digunakan");
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Peran Default User Tidak Ditemukan"));

    
        User user = User.builder()
                .username(dto.getUsername())
                .password(PasswordUtil.hash(dto.getPassword()))
                .status(true)
                .createdAt(LocalDate.now())
                .updateAt(LocalDate.now())
                .email(dto.getEmail())
                .namaLengkap(dto.getNamaLengkap())
                .roles(Collections.singletonList(userRole))
                .build();

        User simpanUser = userRepository.save(user);

        return UserDetailDto.builder()
                .username(simpanUser.getUsername())
                .role(userRole.getRoleName())
                .namaLengkap(simpanUser.getNamaLengkap())
                .build();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
