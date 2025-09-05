package com.perpustakaan.dean.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.perpustakaan.dean.dto.LoanResponseDto;
import com.perpustakaan.dean.enums.LoanStatus;
import com.perpustakaan.dean.service.BookService;
import com.perpustakaan.dean.service.LoanService;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final BookService bookService;
    private final LoanService loanService;

    public UserDashboardController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("username", principal.getName());

        List<LoanResponseDto> activeLoans = loanService.getLoansByUser(principal.getName())
                .stream()
                .filter(l -> l.getStatus() == LoanStatus.BORROWED)
                .collect(Collectors.toList());
        model.addAttribute("activeLoans", activeLoans);

        return "dashboard";
    }


    @PostMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId, Principal principal) {
        loanService.borrowBook(bookId, principal.getName());
        return "redirect:/user/dashboard";
    }
}

