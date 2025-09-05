package com.perpustakaan.dean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class LoanRequestDto {
    private Long bookId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private LocalDate dueDate;

}
