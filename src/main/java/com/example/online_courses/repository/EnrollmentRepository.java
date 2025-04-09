package com.example.online_courses.repository;

import com.example.online_courses.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByUserUserIdAndCourseCourseId(UUID userId, UUID courseId);
     List<Enrollment> findByUserUserId(UUID userId);
    
}