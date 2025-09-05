package com.perpustakaan.dean.controllers;

import com.perpustakaan.dean.dto.LupaPasswordRequestDto;
import com.perpustakaan.dean.service.LupaPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class LupaPasswordController {

    private final LupaPasswordService lupaPasswordService;

    @GetMapping("/lupa")
    public String showLupaForm(Model model) {
        model.addAttribute("requestDto", new LupaPasswordRequestDto());
        return "forgot-password"; 
    }

    @PostMapping("/lupa")
    public String requestPasswordReset(@Valid @ModelAttribute("requestDto") LupaPasswordRequestDto request,
                                       Model model) {
        boolean success = lupaPasswordService.requestPasswordReset(request);

        if (success) {
            model.addAttribute("message", "OTP telah dikirim ke email/username Anda!");
        } else {
            model.addAttribute("error", "Username tidak ditemukan!");
        }

        return "forgot-password";
    }
}


