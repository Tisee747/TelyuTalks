package com.telyutalks.telyutalks.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telyutalks.telyutalks.model.Question;
import com.telyutalks.telyutalks.model.User;
import com.telyutalks.telyutalks.repository.QuestionRepository;
import com.telyutalks.telyutalks.repository.UserRepository;

@Controller
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/ask")
    public String showAskQuestionForm() { return "ask_question"; }

    @PostMapping("/ask")
    public String processAskQuestion(@RequestParam String content, @AuthenticationPrincipal UserDetails userDetails) {
        User author = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Question question = new Question();
        question.setContent(content);
        question.setAuthor(author);
        question.setCreatedAt(LocalDateTime.now());
        Question savedQuestion = questionRepository.save(question);
        return "redirect:/question/" + savedQuestion.getId();
    }

    @GetMapping("/question/{id}")
    public String showQuestionDetail(@PathVariable Long id, Model model) {
        Question question = questionRepository.findByIdWithAnswers(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        
        // Sekarang kita bisa dengan aman mengirim entity Question ke template
        model.addAttribute("question", question);
        
        return "question_detail";
    }
    
    
    @GetMapping("/question/{id}/edit")
    public String showEditQuestionForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));
        // ***** PENGECEKAN KEAMANAN MANUAL *****
        if (!question.getAuthor().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("403 Forbidden");
        }
        model.addAttribute("question", question);
        return "edit_question";
    }

    @PostMapping("/question/{id}/edit")
    public String processEditQuestion(@PathVariable Long id, @RequestParam String content, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));
        // ***** PENGECEKAN KEAMANAN MANUAL *****
        if (!question.getAuthor().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("403 Forbidden");
        }
        question.setContent(content);
        questionRepository.save(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully.");
        return "redirect:/question/" + id;
    }

    @PostMapping("/question/{id}/delete")
    public String deleteQuestion(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));
        // ***** PENGECEKAN KEAMANAN MANUAL *****
        if (!question.getAuthor().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("403 Forbidden");
        }
        questionRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Your question has been deleted.");
        return "redirect:/";
    }
}
