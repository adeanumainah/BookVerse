package com.perpustakaan.dean.controllers;

import com.perpustakaan.dean.dto.BookResponseDto;
import com.perpustakaan.dean.dto.CategoryResponseDto;
import com.perpustakaan.dean.dto.LoanRequestDto;
import com.perpustakaan.dean.dto.LoanResponseDto;
import com.perpustakaan.dean.service.BookService;
import com.perpustakaan.dean.service.CategoryService;
import com.perpustakaan.dean.service.LoanService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserBookController {

    private final BookService bookService;
    private final LoanService loanService;
    private final CategoryService categoryService;
 
    @GetMapping("/books")
    public String showBooks(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "year", required = false) String year,
            Model model) {

        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        List<BookResponseDto> books = bookService.searchBooks(q, categoryId, year);
        model.addAttribute("books", books);

        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("year", year);

        return "dashboard";
    }

    @PostMapping("/books/{bookId}/loan")
    @ResponseBody
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, Principal principal) {
        String username = principal.getName();

        LoanRequestDto dto = new LoanRequestDto();
        dto.setBookId(bookId);
        dto.setLoanDate(LocalDate.now());
        dto.setDueDate(LocalDate.now().plusDays(7));

        loanService.createLoan(dto, username);

        return ResponseEntity.ok("Buku berhasil dipinjam!");
    }

    @GetMapping("/loans")
    public String showLoans(Model model, Principal principal) {
        String username = principal.getName(); 
        List<LoanResponseDto> loans = loanService.getLoansByUser(username);
        model.addAttribute("loans", loans);
        return "user-loan"; 
    }


    @PostMapping("/loans/{id}/return-request")
    @ResponseBody
    public ResponseEntity<String> requestReturn(@PathVariable Long id, Principal principal) {
        loanService.requestReturn(id, principal.getName());
        return ResponseEntity.ok("Pengembalian berhasil diajukan, menunggu konfirmasi admin.");
    }

}
