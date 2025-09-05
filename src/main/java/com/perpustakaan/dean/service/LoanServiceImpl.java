package com.perpustakaan.dean.service;

import com.perpustakaan.dean.dto.LoanRequestDto;
import com.perpustakaan.dean.dto.LoanResponseDto;
import com.perpustakaan.dean.enums.LoanStatus;
import com.perpustakaan.dean.models.Book;
import com.perpustakaan.dean.models.Loan;
import com.perpustakaan.dean.models.User;
import com.perpustakaan.dean.repositories.BookRepository;
import com.perpustakaan.dean.repositories.LoanRepository;
import com.perpustakaan.dean.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public LoanResponseDto createLoan(LoanRequestDto requestDto, String username) {
        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .dueDate(requestDto.getDueDate() != null ? requestDto.getDueDate() : LocalDate.now().plusDays(7))
                .returnDate(requestDto.getReturnDate())
                .status(LoanStatus.BORROWED)
                .build();

        Loan savedLoan = loanRepository.save(loan);
        return mapToDto(savedLoan);
    }

    @Override
    public List<LoanResponseDto> getLoansByUser(String username) {
        return loanRepository.findByUserUsername(username)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LoanResponseDto returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setReturnDate(LocalDate.now());

        // cek overdue
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            loan.setStatus(LoanStatus.OVERDUE);
        } else {
            loan.setStatus(LoanStatus.RETURNED);
        }

        Loan updatedLoan = loanRepository.save(loan);
        return mapToDto(updatedLoan);
    }

    @Override
    public List<LoanResponseDto> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LoanResponseDto getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        return mapToDto(loan);
    }

    private LoanResponseDto mapToDto(Loan loan) {
        LoanResponseDto dto = new LoanResponseDto();
        dto.setId(loan.getId());
        dto.setBookTitle(loan.getBook().getTitle());
        dto.setUserEmail(loan.getUser().getEmail());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus());
        return dto;
    }

    public void borrowBook(Long bookId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setStatus(LoanStatus.BORROWED);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(7));

        loanRepository.save(loan);
    }

    @Override
    public LoanResponseDto requestReturn(Long loanId, String username) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Tidak boleh ajukan pengembalian pinjaman user lain!");
        }

        loan.setStatus(LoanStatus.RETURN_REQUESTED);
        Loan updatedLoan = loanRepository.save(loan);

        return mapToDto(updatedLoan);
    }

    @Override
    public LoanResponseDto confirmReturn(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getStatus().equals(LoanStatus.RETURN_REQUESTED)) {
            throw new RuntimeException("Loan tidak dalam status RETURN_REQUESTED");
        }

        loan.setReturnDate(LocalDate.now());

        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            loan.setStatus(LoanStatus.OVERDUE);
        } else {
            loan.setStatus(LoanStatus.RETURNED);
        }

        Loan updatedLoan = loanRepository.save(loan);
        return mapToDto(updatedLoan);
    }

    @Override
    public List<LoanResponseDto> getReturnRequests() {
        return loanRepository.findByStatus(LoanStatus.RETURN_REQUESTED)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public Page<LoanResponseDto> getAllLoansPage(int page, int size) {
        Page<Loan> p = loanRepository.findAll(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
        return p.map(this::mapToDto);
    }

    @Override
    public Page<LoanResponseDto> getLoansByStatus(LoanStatus status, int page, int size) {
        Page<Loan> p = loanRepository.findByStatus(
            status,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
        return p.map(this::mapToDto);
    }

}
