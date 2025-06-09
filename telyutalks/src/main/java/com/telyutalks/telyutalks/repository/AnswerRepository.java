package com.telyutalks.telyutalks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telyutalks.telyutalks.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // ***** NEW METHOD *****
    // Finds all answers posted by a user with a specific ID.
    List<Answer> findByAuthorId(Long authorId);

    @Query(value = "SELECT * FROM answer a WHERE a.content LIKE %:keyword%", nativeQuery = true)
    List<Answer> searchByContent(@Param("keyword") String keyword);
}