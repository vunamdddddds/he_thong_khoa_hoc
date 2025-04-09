package com.example.online_courses.controller;

import com.example.online_courses.entity.Course;
import com.example.online_courses.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseApiController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/search-suggestions")
    public List<Course> searchSuggestions(@RequestParam("query") String query) {
        try {
            System.out.println("Received search query: " + query); // Log để debug
            List<Course> courses = courseService.searchSuggestions(query);
            System.out.println("Found " + courses.size() + " courses");
            return courses;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching course suggestions: " + e.getMessage());
        }
    }
}