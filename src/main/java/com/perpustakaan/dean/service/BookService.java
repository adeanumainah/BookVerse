package com.perpustakaan.dean.service;

import com.perpustakaan.dean.dto.BookRequestDto;
import com.perpustakaan.dean.dto.BookResponseDto;
import com.perpustakaan.dean.models.Book;
import com.perpustakaan.dean.models.Category;
import com.perpustakaan.dean.repositories.BookRepository;
import com.perpustakaan.dean.repositories.CategoryRepository;
import com.perpustakaan.dean.repositories.LoanRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final LoanRepository loanRepository;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/covers/";

    public BookResponseDto createBook(BookRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan"));

        String fileName = null;
        if (dto.getCoverFile() != null && !dto.getCoverFile().isEmpty()) {
            try {
                fileName = System.currentTimeMillis() + "_" + dto.getCoverFile().getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                dto.getCoverFile().transferTo(path.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Gagal upload file: " + e.getMessage());
            }
        }

        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .cover(fileName) 
                .category(category)
                .releaseYear(dto.getReleaseYear())
                .build();

        Book saved = bookRepository.save(book);
        return toDTO(saved);
    }

    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Buku tidak ditemukan"));
        return toDTO(book);
    }


    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Buku tidak ditemukan"));

        boolean pernahDipinjam = loanRepository.existsByBook_Id(id);
        if (pernahDipinjam) {
            throw new IllegalStateException("Buku tidak bisa dihapus karena sedang atau pernah dipinjam pengguna.");
        }

        bookRepository.delete(book);
    }

    public BookResponseDto updateBook(Long id, BookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Buku tidak ditemukan"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan"));

        if (dto.getCoverFile() != null && !dto.getCoverFile().isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + dto.getCoverFile().getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                dto.getCoverFile().transferTo(path.toFile());
                book.setCover(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Gagal upload file: " + e.getMessage());
            }
        }

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setCategory(category);
        book.setReleaseYear(dto.getReleaseYear()); 

        return toDTO(bookRepository.save(book));
    }

    private BookResponseDto toDTO(Book book) {
        String coverFileName = null;
        if (book.getCover() != null) {
            String originalCover = book.getCover();
            coverFileName = Paths.get(originalCover).getFileName().toString();
        }
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory().getName(),
                coverFileName,
                book.getReleaseYear());
    }

    public List<BookResponseDto> searchBooks(String q, Long categoryId, String year) {
        Specification<Book> spec = Specification.where(null);

        if (StringUtils.hasText(q)) {
            String like = "%" + q.toLowerCase() + "%";
            Specification<Book> titleSpec = (root, query, cb) -> cb.like(cb.lower(root.get("title")), like);
            spec = spec.and(titleSpec);
        }

        if (categoryId != null) {
            Specification<Book> catSpec = (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
            spec = spec.and(catSpec);
        }

        List<Book> found = bookRepository.findAll(spec, Sort.by("title").ascending());
        return found.stream().map(this::toDTO).collect(Collectors.toList());
    }

}
