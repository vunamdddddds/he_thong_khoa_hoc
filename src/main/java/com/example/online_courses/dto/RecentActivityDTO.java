package com.example.online_courses.dto;

import java.time.LocalDateTime;

public class RecentActivityDTO {
    private String activity; // e.g., "Mua khóa học [Tên khóa học]"
    private String userEmail; // Email của người dùng (ẩn một phần)
    private LocalDateTime timestamp; // Thời gian giao dịch

    // Constructor
    public RecentActivityDTO(String activity, String userEmail, LocalDateTime timestamp) {
        this.activity = activity;
        this.userEmail = userEmail;
        this.timestamp = timestamp;
    }

    // Getters và Setters
    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}