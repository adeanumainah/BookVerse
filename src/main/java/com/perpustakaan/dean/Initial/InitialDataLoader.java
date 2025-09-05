package com.perpustakaan.dean.Initial;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.perpustakaan.dean.models.Role;
import com.perpustakaan.dean.models.User;
import com.perpustakaan.dean.repositories.RoleRepository;
import com.perpustakaan.dean.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;
@Component
public class InitialDataLoader {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        try {
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN", true)));


            
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User user = new User();
                user.setEmail("admin@gmail.com");
                user.setUsername("admin");
                user.setNamaLengkap("Administrator");
                user.setPassword(passwordEncoder.encode("12345678"));
                user.setStatus(true);
                user.setCreatedAt(LocalDate.now());
                user.setUpdateAt(LocalDate.now());
                user.setRoles(List.of(adminRole));
                userRepository.save(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


