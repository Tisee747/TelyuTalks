package com.telyutalks.telyutalks.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telyutalks.telyutalks.model.Answer;
import com.telyutalks.telyutalks.model.Question;
import com.telyutalks.telyutalks.model.User;
import com.telyutalks.telyutalks.repository.AnswerRepository;
import com.telyutalks.telyutalks.repository.QuestionRepository;
import com.telyutalks.telyutalks.repository.UserRepository;

@Controller
public class AnswerController {
    @Autowired private AnswerRepository answerRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping("/question/{questionId}/answers")
    public String processAddAnswer(@PathVariable Long questionId, @RequestParam String content, @AuthenticationPrincipal UserDetails userDetails) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        User author = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(question);
        answer.setAuthor(author);
        answer.setCreatedAt(LocalDateTime.now());
        answerRepository.save(answer);
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/answer/{id}/edit")
    public String processEditAnswer(@PathVariable Long id, @RequestParam String content, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Answer answer = answerRepository.findById(id).orElseThrow(() -> new RuntimeException("Answer not found"));
        answer.setContent(content);
        answerRepository.save(answer);
        redirectAttributes.addFlashAttribute("successMessage", "Your answer has been updated.");
        return "redirect:/question/" + answer.getQuestion().getId();
    }

    @PostMapping("/answer/{id}/delete")
    public String deleteAnswer(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Answer answer = answerRepository.findById(id).orElseThrow(() -> new RuntimeException("Answer not found"));
        Long questionId = answer.getQuestion().getId();
        answerRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Your answer has been deleted.");
        return "redirect:/question/" + questionId;
    }
}