// SALIN DAN GANTI SELURUH ISI FILE AdminController.java DENGAN KODE DI BAWAH INI

package com.telyutalks.telyutalks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telyutalks.telyutalks.model.Answer;
import com.telyutalks.telyutalks.model.Question;
import com.telyutalks.telyutalks.model.Report;
import com.telyutalks.telyutalks.model.User;
import com.telyutalks.telyutalks.repository.AnswerRepository;
import com.telyutalks.telyutalks.repository.QuestionRepository;
import com.telyutalks.telyutalks.repository.ReportRepository;
import com.telyutalks.telyutalks.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private ReportRepository reportRepository;

    @ModelAttribute
    public void addCommonAttributes(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User admin = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            model.addAttribute("loggedInAdmin", admin);
        }
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login_admin";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("studentCount", userRepository.countByRole("STUDENT"));
        model.addAttribute("lecturerCount", userRepository.countByRole("LECTURER"));
        model.addAttribute("questionCount", questionRepository.count());
        model.addAttribute("answerCount", answerRepository.count());
        model.addAttribute("reportCount", reportRepository.count());
        model.addAttribute("activePage", "dashboard"); // Ditambahkan
        return "admin/dashboard";
    }

    @GetMapping("/posts")
    public String showPostsMenu(Model model) {
        model.addAttribute("questionCount", questionRepository.count());
        model.addAttribute("answerCount", answerRepository.count());
        model.addAttribute("activePage", "posts"); // Ditambahkan
        return "admin/posts_menu";
    }

    @GetMapping("/posts/questions")
    public String showAllQuestions(@RequestParam(required = false) String query, Model model) {
        List<Question> questions;
        if (query != null && !query.isEmpty()) {
            questions = questionRepository.searchByContent(query);
        } else {
            questions = questionRepository.findAll();
        }
        model.addAttribute("questions", questions);
        model.addAttribute("query", query);
        model.addAttribute("activePage", "posts"); // Ditambahkan
        return "admin/posts_list_questions";
    }

    @GetMapping("/posts/answers")
    public String showAllAnswers(@RequestParam(required = false) String query, Model model) {
        List<Answer> answers;
        if (query != null && !query.isEmpty()) {
            answers = answerRepository.searchByContent(query);
        } else {
            answers = answerRepository.findAll();
        }
        model.addAttribute("answers", answers);
        model.addAttribute("query", query);
        model.addAttribute("activePage", "posts");
        return "admin/posts_list_answers";
    }


    @GetMapping("/posts/view/{id}")
    public String viewAdminPostDetail(@PathVariable Long id, Model model) {
        Question question = questionRepository.findById(id).orElse(null);
        if(question == null) return "redirect:/admin/posts";
        model.addAttribute("question", question);
        model.addAttribute("activePage", "posts");
        return "admin/posts_view";
    }

    @PostMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        questionRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Question and all its answers have been deleted.");
        return "redirect:/admin/posts";
    }

    @PostMapping("/answers/delete/{id}")
    public String deleteAnswer(@PathVariable Long id, @RequestParam(name="returnTo", defaultValue="") String returnTo, RedirectAttributes redirectAttributes) {
        Answer answer = answerRepository.findById(id).orElse(null);
        if (answer != null) {
            Long questionId = answer.getQuestion().getId();
            answerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Answer has been deleted.");
            
            if ("list".equals(returnTo)) {
                return "redirect:/admin/posts/answers";
            }
            return "redirect:/admin/posts/view/" + questionId;
        }
        return "redirect:/admin/posts";
    }
    
    @GetMapping("/reports")
    public String showReportsMenu(Model model) {
        model.addAttribute("questionReportCount", reportRepository.countByPostType(Report.PostType.QUESTION));
        model.addAttribute("answerReportCount", reportRepository.countByPostType(Report.PostType.ANSWER));
        model.addAttribute("activePage", "reports"); // Ditambahkan
        return "admin/reports_menu";
    }
    @GetMapping("/reports/view/{reportId}")
    public String viewReportDetail(@PathVariable Long reportId, Model model, RedirectAttributes redirectAttributes) {
        Report report = reportRepository.findById(reportId).orElse(null);

        if (report == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Report not found.");
            return "redirect:/admin/reports";
        }

        if (report.getPostType() == Report.PostType.QUESTION) {
            Question question = questionRepository.findById(report.getPostId()).orElse(null);
            model.addAttribute("question", question);
            model.addAttribute("report", report);
            return "admin/reports_view_questions";
        } else if (report.getPostType() == Report.PostType.ANSWER) {
            Answer answer = answerRepository.findByIdWithQuestion(report.getPostId()).orElse(null);
            if (answer != null) {
                model.addAttribute("question", answer.getQuestion());
                model.addAttribute("reportedAnswer", answer);
                model.addAttribute("report", report);
            }
            return "admin/reports_view_answers";
        }
        return "redirect:/admin/reports";
    }
    
    @GetMapping("/reports/list")
    public String showReportList(@RequestParam("type") String type, Model model) {
        try {
            if ("question".equalsIgnoreCase(type)) {
                List<Report> reports = reportRepository.findByPostType(Report.PostType.QUESTION);
                model.addAttribute("reports", reports);
                return "admin/reports_list_questions";
            } else if ("answer".equalsIgnoreCase(type)) {
                List<Report> reports = reportRepository.findByPostType(Report.PostType.ANSWER);
                model.addAttribute("reports", reports);
                return "admin/reports_list_answers";
            }
        } catch (IllegalArgumentException e) {
            // Tangani jika tipe tidak valid
        }
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/resolve/{id}")
    public String resolveReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Report report = reportRepository.findById(id).orElse(null);
        String type = "question"; 

        if (report != null) {
            type = report.getPostType().name().toLowerCase();
        }

        reportRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Report has been deleted.");
        
        return "redirect:/admin/reports/list?type=" + type;
    }

    @PostMapping("/reports/delete/{id}")
    public String deleteReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String type = "question"; // default
        Report report = reportRepository.findById(id).orElse(null);
        if (report != null) {
            type = report.getPostType().name().toLowerCase();
        }
        reportRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Report has been deleted.");
        return "redirect:/admin/reports/list?type=" + type;
    }


    // Metode untuk /accounts yang mungkin belum ada atau belum lengkap
    @GetMapping("/accounts")
    public String showAllAccounts(@RequestParam(required = false) String query, Model model) {
        List<User> users;
        if (query != null && !query.isEmpty()) {
            users = userRepository.findByNamaContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
        } else {
            users = userRepository.findAll();
        }
        model.addAttribute("users", users);
        model.addAttribute("query", query);
        model.addAttribute("activePage", "accounts");
        return "admin/accounts";
    }

    // Metode untuk /profile/{id} yang mungkin belum ada atau belum lengkap
    @GetMapping("/profile/{id}")
    public String viewUserProfile(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/accounts";
        }
        model.addAttribute("user", user);
        model.addAttribute("activePage", "accounts");
        return "admin/profile_view"; 
    }
}