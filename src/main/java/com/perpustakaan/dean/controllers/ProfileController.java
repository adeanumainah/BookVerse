package com.perpustakaan.dean.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.perpustakaan.dean.dto.LoanResponseDto;
import com.perpustakaan.dean.enums.LoanStatus;
import com.perpustakaan.dean.models.User;
import com.perpustakaan.dean.repositories.UserRepository;
import com.perpustakaan.dean.service.LoanService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;
    private final LoanService loanService;


    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

    model.addAttribute("user", user);

    List<LoanResponseDto> activeLoans = loanService.getLoansByUser(principal.getName())
            .stream()
            .filter(l -> l.getStatus() == LoanStatus.BORROWED)
            .collect(Collectors.toList());
    model.addAttribute("activeLoans", activeLoans);

    return "profile"; 
}


    @GetMapping("/profile/edit")
    public String editProfileForm(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "edit-profile"; 
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setNamaLengkap(updatedUser.getNamaLengkap());

        userRepository.save(user);
        return "redirect:/user/profile?updated";
    }
}

