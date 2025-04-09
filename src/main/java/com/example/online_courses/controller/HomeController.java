package com.example.online_courses.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.online_courses.service.CourseService;

@Controller
public class HomeController {

    @Autowired
    private CourseService courseService;




    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("courses", courseService.getTop3LatestCourses());

        return "home"; // Trả về home.html
    }
    @GetMapping("/old-news")
    public String news() {
        return "news"; // Trả về news.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Trả về login.html
    }
}