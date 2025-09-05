package com.perpustakaan.dean.dto;

import com.perpustakaan.dean.enums.LoanStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanResponseDto {
    private Long id;
    private String bookTitle;
    private String userEmail;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
}
