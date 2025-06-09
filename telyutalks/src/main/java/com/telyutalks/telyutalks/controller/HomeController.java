package com.telyutalks.telyutalks.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.telyutalks.telyutalks.model.Question;
import com.telyutalks.telyutalks.repository.QuestionRepository;

@Controller
public class HomeController {

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/")
    public String home(Model model) {
        // For now, it will fetch an empty list, which is fine.
        List<Question> questions = questionRepository.findAll(); 
        model.addAttribute("questions", questions);
        return "home"; // Renders home.html
    }

    @GetMapping("/search")
    public String searchQuestions(@RequestParam("query") String query, Model model) {
        // Sekarang menggunakan metode repository yang sudah diperbarui dan andal.
        List<Question> searchResults = questionRepository.searchByContent(query);
        
        model.addAttribute("results", searchResults);
        model.addAttribute("query", query);
        
        // Mengarahkan ke halaman hasil pencarian.
        return "search_result"; 
    }
}
