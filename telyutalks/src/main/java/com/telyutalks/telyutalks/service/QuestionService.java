package com.telyutalks.telyutalks.service;

import com.telyutalks.telyutalks.model.Question;
import com.telyutalks.telyutalks.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepo;

    public QuestionService(QuestionRepository questionRepo) {
        this.questionRepo = questionRepo;
    }

    public List<Question> getAllUsers() {
        return questionRepo.findAll();
    }
}
