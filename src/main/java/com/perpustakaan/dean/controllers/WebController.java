package com.perpustakaan.dean.controllers;

import com.perpustakaan.dean.dto.*;
import com.perpustakaan.dean.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class WebController {

    private final UserService userService;
    private final LoginService loginService;
    private final LupaPasswordService lupaPasswordService;
    private final BookService bookService;
    private final LoanService loanService;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new ListUserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") ListUserDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.existsByUsername(dto.getUsername())) {
            bindingResult.rejectValue("username", "username.exists", "Username sudah digunakan");
            return "register";
        }
        if (userService.existsByEmail(dto.getEmail())) {
            bindingResult.rejectValue("email", "email.exists", "Email sudah digunakan");
            return "register";
        }

        try {
            userService.daftar(dto); 
            return "redirect:/web/login?registered=true";
        } catch (ResponseStatusException e) {
            model.addAttribute("error", e.getReason());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan server");
            return "register";
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("login") LoginDto dto, Model model) {
        try {
            return loginService.loginWeb(dto);
        } catch (ResponseStatusException e) {
            model.addAttribute("error", e.getReason());
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan");
            return "login";
        }
    }

    @GetMapping("/admin-dashboard")
    public String dashboardPage() {
        return "admin-dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("username", principal.getName());
        return "dashboard";
    }

    @PostMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId, Principal principal) {
        loanService.borrowBook(bookId, principal.getName());
        return "redirect:/web/dashboard";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("lupaPasswordRequestDto", new LupaPasswordRequestDto());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@ModelAttribute LupaPasswordRequestDto request, RedirectAttributes ra) {
        lupaPasswordService.requestPasswordReset(request);
        ra.addFlashAttribute("message", "OTP sudah dikirim ke email anda");
        return "redirect:/web/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(Model model) {
        model.addAttribute("otpResetPasswordDto", new OtpResetPasswordDto());
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@ModelAttribute OtpResetPasswordDto dto, RedirectAttributes ra) {
        lupaPasswordService.validasiOtpDanResetPassword(dto);
        ra.addFlashAttribute("message", "Password berhasil direset, silakan login");
        return "redirect:/web/login";
    }

}
