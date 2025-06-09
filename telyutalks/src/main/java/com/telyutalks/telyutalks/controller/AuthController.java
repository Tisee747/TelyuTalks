package com.telyutalks.telyutalks.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telyutalks.telyutalks.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String viewLoginPage() { return "login"; }

    @GetMapping("/register")
    public String viewRegisterPage() { return "register"; }

    @PostMapping("/register/student")
    public String processStudentRegistration(
            @RequestParam String username, @RequestParam String nama, @RequestParam String nim, @RequestParam String email,
            @RequestParam String password, @RequestParam String fakultas,
            @RequestParam String programStudi, @RequestParam String angkatan,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.registerStudent(username, nama, nim, email, password, fakultas, programStudi, angkatan);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/register/lecturer")
    public String processLecturerRegistration(
            @RequestParam String username,@RequestParam String nama, @RequestParam String nidn, @RequestParam String email,
            @RequestParam String password, @RequestParam String fakultas,
            RedirectAttributes redirectAttributes) {
        System.out.println("[CONTROLLER] Mencoba mendaftarkan dosen dengan username: " + username);
        try {
            userService.registerLecturer(username, nama, nidn, email, password, fakultas);
            System.out.println("[CONTROLLER] Registrasi dosen BERHASIL untuk: " + username);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            System.err.println("[CONTROLLER] !!! REGISTRASI DOSEN GAGAL untuk " + username + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
}