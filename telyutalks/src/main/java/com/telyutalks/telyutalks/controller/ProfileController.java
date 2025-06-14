package com.telyutalks.telyutalks.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telyutalks.telyutalks.model.Answer;
import com.telyutalks.telyutalks.model.Lecturer;
import com.telyutalks.telyutalks.model.Question;
import com.telyutalks.telyutalks.model.Student;
import com.telyutalks.telyutalks.model.User;
import com.telyutalks.telyutalks.repository.AnswerRepository;
import com.telyutalks.telyutalks.repository.QuestionRepository;
import com.telyutalks.telyutalks.repository.ReportRepository;
import com.telyutalks.telyutalks.repository.UserRepository;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired private UserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private ReportRepository reportRepository;

    private Map<String, List<String>> getProdiData() {
        Map<String, List<String>> prodiData = new LinkedHashMap<>();
        prodiData.put("Fakultas Informatika", List.of("S-1 Informatika", "S-1 Teknologi Informasi", "S-1 Rekayasa Perangkat Lunak", "S-1 Sains Data", "S-1 PJJ Informatika", "S-2 Informatika", "S-2 Keamanan Siber & Forensik Digital", "S-3 Informatika"));
        prodiData.put("Fakultas Teknik Elektro", List.of("S-1 Teknik Telekomunikasi", "S-1 Teknik Elektro", "S-1 Teknik Komputer", "S-1 Teknik Fisika", "S-1 Teknik Biomedis", "S-1 Teknik Sistem Energi", "S-2 Teknik Elektro", "S-3 Teknik Elektro"));
        prodiData.put("Fakultas Rekayasa Industri", List.of("S1-Teknik Industri", "S1-Sistem Informasi", "S1-Teknik Logistik", "S1-Manajemen Rekayasa", "S2-Teknik Industri", "S2-Sistem Informasi"));
        prodiData.put("Fakultas Ekonomi dan Bisnis", List.of("S1-Manajemen", "S1-Akuntasi", "S1-Manajemen Bisnis Rekreasi", "S1-Bisnis Digital", "S1-Administrasi Bisnis", "S2-Administrasi Bisnis", "S2-PJJ Manajemen", "S2-Manajemen", "S2-Akuntasi"));
        prodiData.put("Fakultas Komunikasi dan Ilmu Sosial", List.of("S1-Ilmu Komunikasi", "S1-Hubungan Masyarakat", "S1-Digital Content Brodcasting", "S1-Psikologi", "S2-Ilmu Komunikasi"));
        prodiData.put("Fakultas Industri Kreatif", List.of("S1-Desain Komunikasi Visual", "S1-Desain Produk", "S1-Desain Interior", "S1-Seni Rupa", "S1-Kriya", "S1-Film dan Animasi", "S2-Desain"));
        prodiData.put("Fakultas Ilmu Terapan", List.of("D3-Teknik Telekomunikasi", "D3-Rekayasa Perangkat Lunak Aplikasi", "D3-Sistem Informasi", "D3-Sistem Informasi Akuntansi", "D3-Teknologi Komputer", "D3-Digital Marketing", "D3-Hospitality & Culinary Art", "D4-Digital Creative Multimedia", "D4-Sistem Informasi Kota Cerdas"));
        return prodiData;
    }

    @GetMapping("/profile")
    public String userProfile(
            @RequestParam(name = "view", defaultValue = "answers") String view,
            Model model, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Question> userQuestions = questionRepository.findByAuthorId(currentUser.getId());
        List<Answer> userAnswers = answerRepository.findByAuthorId(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("questions", userQuestions);
        model.addAttribute("answers", userAnswers);
        model.addAttribute("activeView", view);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", currentUser);
        model.addAttribute("prodiData", getProdiData());
        return "edit_profile";
    }

    @PostMapping("/profile/edit")
    public String processEditProfile(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam String nama,
        @RequestParam String fakultas,
        @RequestParam(required = false) String programStudi,
        @RequestParam(required = false) String angkatan,
        @RequestParam(required = false) String nidn,
        RedirectAttributes redirectAttributes
    ) {
        User userToUpdate = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        userToUpdate.setNama(nama);
        userToUpdate.setFakultas(fakultas);

        if (userToUpdate instanceof Student) {
            ((Student) userToUpdate).setProgramStudi(programStudi);
            ((Student) userToUpdate).setAngkatan(angkatan);
        } else if (userToUpdate instanceof Lecturer) {
            ((Lecturer) userToUpdate).setNidn(nidn);
        }

        userRepository.save(userToUpdate);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String username = request.getUserPrincipal().getName();
        User userToDelete = userRepository.findByUsername(username).orElse(null);

        if (userToDelete != null) {
            reportRepository.deleteAllByReporter(userToDelete);

            userRepository.delete(userToDelete);

            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            redirectAttributes.addFlashAttribute("successMessage", "Akun Anda telah berhasil dihapus.");
            return "redirect:/login";
        }
        
        redirectAttributes.addFlashAttribute("errorMessage", "Could not delete profile.");
        return "redirect:/profile/edit";
    }
}
