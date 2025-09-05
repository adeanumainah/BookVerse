package com.perpustakaan.dean.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.perpustakaan.dean.dto.ListUserDto;
import com.perpustakaan.dean.dto.ResponUmum;
import com.perpustakaan.dean.dto.UserDetailDto;
import com.perpustakaan.dean.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/daftar")
    public ResponseEntity<ResponUmum<UserDetailDto>> daftar(@RequestBody ListUserDto dto) {
        try {
            UserDetailDto userDetail = userService.daftar(dto);
            return ResponseEntity.ok(ResponUmum.<UserDetailDto>builder()
                    .berhasil(true)
                    .pesan("User Telah Berhasil Mendaftar")
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
                            .pesan("Kesalahan Server Internal")
                            .data(null)
                            .build());
        }
    }
}