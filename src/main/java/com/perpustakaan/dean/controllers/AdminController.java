package com.perpustakaan.dean.controllers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.perpustakaan.dean.service.*;

import jakarta.servlet.http.HttpServletResponse;

import com.perpustakaan.dean.dto.*;
import com.perpustakaan.dean.enums.LoanStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final BookService bookService;
    private final LoanService loanService;
     private final PdfService pdfService;

    public AdminController(CategoryService categoryService, BookService bookService, LoanService loanService, PdfService pdfService) {
        this.categoryService = categoryService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.pdfService = pdfService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute CategoryRequestDto dto) {
        categoryService.createCategory(dto);
        return "redirect:/admin/categories"; 
    }

    @GetMapping("/categories")
    public String showCategories(Model model) {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "category";
    }

    @PostMapping("/categories/{id}/update")
    public String updateCategory(@PathVariable Long id, @ModelAttribute CategoryRequestDto dto) {
        categoryService.updateCategory(id, dto);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Kategori berhasil dihapus");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan saat menghapus kategori");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/books")
    public String showBooks(Model model) {
        List<BookResponseDto> books = bookService.getAllBooks();
        List<CategoryResponseDto> categories = categoryService.getAllCategories();

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("bookForm", new BookRequestDto());
        return "book";
    }

    @PostMapping("/books/create")
    public String createBook(@ModelAttribute BookRequestDto dto) {
        bookService.createBook(dto);
        return "redirect:/admin/books";
    }



    @PostMapping("/books/{id}/update")
    public String updateBook(@PathVariable Long id,
            @ModelAttribute BookRequestDto dto,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            RedirectAttributes redirectAttributes) {
        try {
            dto.setCoverFile(coverFile);
            bookService.updateBook(id, dto);
            redirectAttributes.addFlashAttribute("success", "Buku berhasil diperbarui!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui buku: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        BookResponseDto book = bookService.getBookById(id);
        List<CategoryResponseDto> categories = categoryService.getAllCategories();

        model.addAttribute("book", book);
        model.addAttribute("categories", categories);

        return "book-edit";
    }


    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Buku berhasil dihapus!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan saat menghapus buku.");
        }
        return "redirect:/admin/books";
    }



    @GetMapping("/loans")
    public String adminLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status, 
            Model model) {

        LoanStatus loanStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                loanStatus = LoanStatus.valueOf(status); 
            } catch (IllegalArgumentException e) {
                loanStatus = null;
            }
        }

        Page<LoanResponseDto> loanPage = (loanStatus == null)
                ? loanService.getAllLoansPage(page, size)
                : loanService.getLoansByStatus(loanStatus, page, size);

        model.addAttribute("loans", loanPage.getContent());
        model.addAttribute("currentPage", loanPage.getNumber());
        model.addAttribute("totalPages", loanPage.getTotalPages());
        model.addAttribute("size", loanPage.getSize());
        model.addAttribute("status", loanStatus != null ? loanStatus.name() : null);

        return "admin-loan";
    }

    @GetMapping("/loans/export")
    public void exportLoansPdf(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "false") boolean all,
            HttpServletResponse response) throws IOException {

        LoanStatus loanStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                loanStatus = LoanStatus.valueOf(status);
            } catch (IllegalArgumentException ex) {
                loanStatus = null;
            }
        }

        List<LoanResponseDto> loans;
        if (all) {
            loans = loanService.getAllLoans();
            if (loanStatus != null) {
                LoanStatus finalStatus = loanStatus;
                loans = loans.stream()
                            .filter(l -> l.getStatus() == finalStatus)
                            .collect(Collectors.toList());
            }
        } else {
            Page<LoanResponseDto> p = (loanStatus == null)
                    ? loanService.getAllLoansPage(page, size)
                    : loanService.getLoansByStatus(loanStatus, page, size);
            loans = p.getContent();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("loans", loans);
        data.put("status", loanStatus != null ? loanStatus.name() : "Semua");
        data.put("generatedAt", java.time.LocalDateTime.now().toString());

        byte[] pdf = pdfService.generatePdfFromTemplate("admin-loan-pdf", data);

        response.setContentType("application/pdf");
        String filename = "loans_" + (loanStatus != null ? loanStatus.name() : "all") + (all ? "_all" : ("_page" + page)) + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }


}
