package com.perpustakaan.dean.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.perpustakaan.dean.dto.LoanRequestDto;
import com.perpustakaan.dean.dto.LoanResponseDto;
import com.perpustakaan.dean.enums.LoanStatus;

public interface LoanService {
    LoanResponseDto createLoan(LoanRequestDto requestDto, String username);
    LoanResponseDto returnBook(Long loanId);
    List<LoanResponseDto> getAllLoans();
    LoanResponseDto getLoanById(Long id);
    List<LoanResponseDto> getLoansByUser(String username);
    void borrowBook(Long bookId, String email);
    LoanResponseDto requestReturn(Long loanId, String username);
    LoanResponseDto confirmReturn(Long loanId);
    List<LoanResponseDto> getReturnRequests();

    Page<LoanResponseDto> getAllLoansPage(int page, int size);
    Page<LoanResponseDto> getLoansByStatus(LoanStatus status, int page, int size);


}
