package com.telyutalks.telyutalks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telyutalks.telyutalks.model.Report;
import com.telyutalks.telyutalks.model.User;
import com.telyutalks.telyutalks.repository.ReportRepository;
import com.telyutalks.telyutalks.repository.UserRepository;

@Controller
public class ReportController {

    @Autowired private ReportRepository reportRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping("/report")
    public String submitReport(
            @RequestParam("postType") Report.PostType postType,
            @RequestParam("postId") Long postId,
            @RequestParam("reason") String reason,
            @RequestParam("returnUrl") String returnUrl,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        User reporter = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Report report = new Report();
        report.setPostType(postType);
        report.setPostId(postId);
        report.setReason(reason);
        report.setReporter(reporter);

        reportRepository.save(report);

        redirectAttributes.addFlashAttribute("successMessage", "Laporan Anda telah berhasil dikirim. Terima kasih.");
        
        return "redirect:" + returnUrl;
    }
}