package com.perpustakaan.dean.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 500)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "nama_lengkap",nullable = false,length = 90)
    private String namaLengkap;

    @Column(nullable = false)
    private Boolean status;

    @Column(name = "created_at", columnDefinition = "DATE")
    private LocalDate createdAt;

    @Column(name = "update_at", columnDefinition = "DATE")
    private LocalDate updateAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}

