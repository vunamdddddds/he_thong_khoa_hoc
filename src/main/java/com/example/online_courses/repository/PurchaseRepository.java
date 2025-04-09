package com.example.online_courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.online_courses.entity.Purchase;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    boolean existsByUserIdAndCourseIdAndStatus(UUID userId, UUID courseId, String status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Purchase p WHERE p.status = 'completed'")
    BigDecimal sumAllPurchases();



    // Lấy danh sách giao dịch có status = 'completed', sắp xếp theo createdAt giảm dần
    List<Purchase> findByStatusOrderByCreatedAtDesc(String status);

    
}