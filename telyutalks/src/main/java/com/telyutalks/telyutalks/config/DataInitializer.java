package com.telyutalks.telyutalks.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.telyutalks.telyutalks.model.Admin;
import com.telyutalks.telyutalks.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findByUsername("admin").isPresent()) {
            System.out.println("Membuat pengguna ADMIN default...");
            
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setNama("Admin TelyuTalks");
            admin.setFakultas("SYSTEM");
            admin.setEmail("admin@telyutalks.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            
            userRepository.save(admin);
            
            System.out.println("Pengguna ADMIN berhasil dibuat dengan username 'admin' dan password 'admin123'");
        }
    }
}
