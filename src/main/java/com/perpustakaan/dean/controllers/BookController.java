package com.perpustakaan.dean.controllers;

import com.perpustakaan.dean.dto.BookRequestDto;
import com.perpustakaan.dean.dto.BookResponseDto;
import com.perpustakaan.dean.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;



@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public String listBooks(Model model) {
        List<BookResponseDto> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "book"; 
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new BookRequestDto());
        return "book-add";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute BookRequestDto bookRequestDto,
                          @RequestParam("coverFile") MultipartFile coverFile) {
        bookRequestDto.setCoverFile(coverFile);
        bookService.createBook(bookRequestDto);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BookResponseDto book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "book-edit"; 
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute BookRequestDto bookRequestDto,
                             @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        bookRequestDto.setCoverFile(coverFile);
        bookService.updateBook(id, bookRequestDto);
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
