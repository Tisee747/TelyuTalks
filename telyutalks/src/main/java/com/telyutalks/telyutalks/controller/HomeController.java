package com.telyutalks.telyutalks.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String landingPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }

        model.addAttribute("questions", questionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        return "landing_page";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("questions", questionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        return "home";
    }

    @GetMapping("/search")
    public String searchQuestions(@RequestParam("query") String query, Model model) {
        List<Question> searchResults = questionRepository.searchByContent(query);
        
        model.addAttribute("results", searchResults);
        model.addAttribute("query", query);
        
        return "search_result"; 
    }
}
