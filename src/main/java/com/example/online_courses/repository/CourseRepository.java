package com.example.online_courses.repository;

import com.example.online_courses.entity.Course;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    
    List<Course> findTop3ByOrderByCreatedAtDesc();


    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}