package com.perpustakaan.dean.repositories;

import com.perpustakaan.dean.models.TokenResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface TokenResetPasswordRepository extends JpaRepository<TokenResetPassword, Integer> {
    Optional<TokenResetPassword> findByUsername(String username);
    void deleteByUsername(String username);
}
