package com.perpustakaan.dean.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.perpustakaan.dean.enums.LoanStatus;
import com.perpustakaan.dean.models.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserUsername(String username);
    List<Loan> findByStatus(LoanStatus status);
    Page<Loan> findAll(Pageable pageable);
    // pagination
    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);
    
    boolean existsByBook_Id(Long bookId);

}
