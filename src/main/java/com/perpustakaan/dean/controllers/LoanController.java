package com.perpustakaan.dean.controllers;

import com.perpustakaan.dean.dto.LoanRequestDto;
import com.perpustakaan.dean.dto.LoanResponseDto;
import com.perpustakaan.dean.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/create")
    public ResponseEntity<LoanResponseDto> createLoan(@RequestBody LoanRequestDto dto,
            Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(loanService.createLoan(dto, username));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<LoanResponseDto> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }

    @GetMapping
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping("/user/loans/{id}/return-request")
    public ResponseEntity<String> requestReturn(@PathVariable Long id, Principal principal) {
        loanService.requestReturn(id, principal.getName());
        return ResponseEntity.ok("Pengembalian berhasil diajukan, menunggu konfirmasi admin.");
    }

    @PutMapping("/admin/confirm-return/{id}")
    public ResponseEntity<LoanResponseDto> confirmReturn(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.confirmReturn(id));
    }


}
