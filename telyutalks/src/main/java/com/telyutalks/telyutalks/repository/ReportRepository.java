package com.telyutalks.telyutalks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.telyutalks.telyutalks.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByPostType(Report.PostType postType);
    long countByPostType(Report.PostType postType);

    @Transactional
    void deleteByPostIdAndPostType(Long postId, Report.PostType postType);
}