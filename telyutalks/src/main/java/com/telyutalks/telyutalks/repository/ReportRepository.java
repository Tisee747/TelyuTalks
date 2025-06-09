package com.telyutalks.telyutalks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telyutalks.telyutalks.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByPostType(Report.PostType postType);
    long countByPostType(Report.PostType postType);
}