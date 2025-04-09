package com.example.online_courses.controller;

import com.example.online_courses.entity.*;
import com.example.online_courses.repository.*;
import com.example.online_courses.service.CourseService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    


    @GetMapping
    public String listCourses(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "9") int size,
                              @RequestParam(value = "search", required = false) String search,
                              Model model) {
        // Lấy danh sách khóa học với phân trang và tìm kiếm
        Page<Course> coursePage = courseService.searchCourses(search, page, size);

        // Lấy thông tin người dùng hiện tại (nếu đã đăng nhập)
        String email = SecurityContextHolder.getContext().getAuthentication() != null && 
                       SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && 
                       !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal()) 
                       ? SecurityContextHolder.getContext().getAuthentication().getName() : null;

        User user = email != null ? userRepository.findByEmail(email).orElse(null) : null;
        UUID userId = user != null ? user.getUserId() : null;

        // Kiểm tra trạng thái đăng ký cho từng khóa học
        if (userId != null) {
            coursePage.forEach(course -> {
                boolean isEnrolled = enrollmentRepository.existsByUserUserIdAndCourseCourseId(userId, course.getCourseId());
                course.setEnrolled(isEnrolled);
            });
        }

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", coursePage.getNumber());
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        return "courses";
    }

    @GetMapping("/{courseId}")
    public String courseDetail(@PathVariable UUID courseId, Model model) {
        // Lấy thông tin khóa học
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "error/404"; // Trang lỗi nếu không tìm thấy khóa học
        }
        model.addAttribute("course", course);

        // Kiểm tra trạng thái mua khóa học
        boolean hasPurchased = false;
        boolean isEnrolled = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                UUID userId = user.getUserId();
                // Kiểm tra trạng thái mua
                hasPurchased = purchaseRepository.existsByUserIdAndCourseIdAndStatus(userId, courseId, "completed");
                // Kiểm tra trạng thái đăng ký
                isEnrolled = enrollmentRepository.existsByUserUserIdAndCourseCourseId(userId, courseId);
            }
        }

        // Nếu khóa học miễn phí và người dùng đã đăng nhập, tự động đăng ký
        if (course.getPrice().equals(BigDecimal.ZERO) && authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser") && !isEnrolled) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                Enrollment enrollment = new Enrollment();
                enrollment.setUser(user);
                enrollment.setCourse(course);
                enrollmentRepository.save(enrollment);
                isEnrolled = true;
            }
        }

        model.addAttribute("hasPurchased", hasPurchased);
        model.addAttribute("isEnrolled", isEnrolled);
        return "course-detail";
    }

    @GetMapping("/my-courses")
    @PreAuthorize("isAuthenticated()")
    public String myCourses(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findByUserUserId(user.getUserId());
        model.addAttribute("enrollments", enrollments);
        return "my-courses";
    }

    @GetMapping("/{id}/learn")
    @PreAuthorize("isAuthenticated()")
    public String learnCourse(@PathVariable("id") UUID id, Model model) {
        try {
            Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID: " + id));
            
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
    
            boolean isEnrolled = enrollmentRepository.existsByUserUserIdAndCourseCourseId(user.getUserId(), id);
            boolean hasPurchased = purchaseRepository.existsByUserIdAndCourseIdAndStatus(user.getUserId(), id, "completed");
            boolean isFree = course.getPrice().equals(BigDecimal.ZERO);
    
            if (!isEnrolled) {
                if (isFree || hasPurchased) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setUser(user);
                    enrollment.setCourse(course);
                    enrollmentRepository.save(enrollment);
                    System.out.println("Enrollment saved successfully for user: " + user.getUserId() + ", course: " + course.getCourseId());
                    isEnrolled = true;
                } else {
                    System.out.println("User not enrolled, redirecting to payment. User: " + user.getUserId() + ", Course: " + course.getCourseId());
                    return "redirect:/courses/" + id + "/payment";
                }
            }
    
            List<Lesson> lessons = lessonRepository.findByCourseCourseIdOrderByOrderIndexAsc(id);
            model.addAttribute("course", course);
            model.addAttribute("lessons", lessons);
            model.addAttribute("hasPurchased", hasPurchased);
            model.addAttribute("isEnrolled", isEnrolled);
            return "course-learn";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/courses/" + id + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/{id}/payment")
    @PreAuthorize("isAuthenticated()")
    public String getPaymentPage(@PathVariable("id") UUID id, Model model) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));
        model.addAttribute("course", course);
        return "course-payment";
    }

    @PostMapping("/{id}/purchase")
    @PreAuthorize("isAuthenticated()")
    public String processPurchase(@PathVariable("id") UUID id, @RequestParam("paymentMethod") String paymentMethod, Model model) {
        try {
            Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID: " + id));
    
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
    
            Purchase purchase = new Purchase();
            purchase.setUserId(user.getUserId());
            purchase.setCourseId(id);
            purchase.setAmount(course.getPrice());
            purchase.setPaymentMethod(paymentMethod);
            purchase.setStatus("completed");
            purchase.setTransactionId("TRANS-" + UUID.randomUUID().toString());
    
            // Lưu Purchase và kiểm tra
            purchaseRepository.save(purchase);
            System.out.println("Purchase saved successfully: " + purchase.getPurchaseId());
    
            return "redirect:/courses/" + id + "/learn";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error during purchase: " + e.getMessage());
            System.out.println("lỗi tùm lum");
            return "error"; // Tạo một trang error.html để hiển thị lỗi
        }
    }


}