package com.telyutalks.telyutalks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telyutalks.telyutalks.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByAuthorId(Long authorId);

    @Query(value = "SELECT * FROM answer a WHERE a.content LIKE :keyword", nativeQuery = true)
    List<Answer> searchByContent(@Param("keyword") String keyword);
    @Query("SELECT a FROM Answer a JOIN FETCH a.question WHERE a.id = :id")
    Optional<Answer> findByIdWithQuestion(@Param("id") Long id);
}