package com.telyutalks.telyutalks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telyutalks.telyutalks.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.content LIKE %:keyword%")
    List<Question> searchByContent(@Param("keyword") String keyword);

    List<Question> findByAuthorId(Long authorId);

    @Query("SELECT q FROM Question q " +
           "LEFT JOIN FETCH q.author " +
           "LEFT JOIN FETCH q.answers a " +
           "LEFT JOIN FETCH a.author " +
           "WHERE q.id = :id")
    Optional<Question> findByIdWithAnswers(@Param("id") Long id);

}