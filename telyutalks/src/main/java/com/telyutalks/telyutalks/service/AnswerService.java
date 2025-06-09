package com.telyutalks.telyutalks.service;

import com.telyutalks.telyutalks.model.Answer;
import com.telyutalks.telyutalks.repository.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepository answerRepo;

    public AnswerService(AnswerRepository answerRepo) {
        this.answerRepo = answerRepo;
    }

    public List<Answer> getAllUsers() {
        return answerRepo.findAll();
    }
}
