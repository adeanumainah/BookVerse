package com.perpustakaan.dean.models;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_reset_password",
       indexes = {@Index(name = "idx_trp_username", columnList = "username", unique = true)})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String token; // simpan hash OTP

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private LocalDateTime tanggalKadaluarsa;

    @Builder.Default
    @Column(nullable = false)
    private Integer attempts = 0;
}
