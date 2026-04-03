package com.kustaurant.kustaurant.admin.adminPage.controller;

import com.kustaurant.kustaurant.admin.adminPage.controller.request.HomeModalUpdateRequest;
import com.kustaurant.kustaurant.admin.adminPage.controller.request.ModalPreviewRequest;
import com.kustaurant.kustaurant.admin.adminPage.controller.response.AdminStatsResponse;
import com.kustaurant.kustaurant.admin.adminPage.controller.response.HomeModalResponse;
import com.kustaurant.kustaurant.admin.adminPage.controller.response.PagedFeedbackResponse;
import com.kustaurant.kustaurant.admin.adminPage.controller.response.PagedRestaurantResponse;
import com.kustaurant.kustaurant.admin.adminPage.controller.response.PagedUserResponse;
import com.kustaurant.kustaurant.admin.adminPage.service.AdminService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Hidden
@Controller
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final TemplateEngine templateEngine;

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String home() {
        return "admin/admin";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/api/stats")
    @ResponseBody
    public ResponseEntity<AdminStatsResponse> getAdminStats() {
        AdminStatsResponse stats = adminService.getAdminStats();
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/api/users/new")
    @ResponseBody
    public ResponseEntity<PagedUserResponse> getNewUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 20)); // 최대 20개로 제한
        PagedUserResponse response = adminService.getNewUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/api/users/all")
    @ResponseBody
    public ResponseEntity<PagedUserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 50)); // 최대 50개로 제한
        PagedUserResponse response = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/api/users/new/count")
    @ResponseBody
    public ResponseEntity<Long> getNewUsersCount() {
        Long count = adminService.getNewUsersCount();
        return ResponseEntity.ok(count);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/api/restaurants")
    @ResponseBody
    public ResponseEntity<PagedRestaurantResponse> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 20)); // 최대 20개로 제한
        PagedRestaurantResponse response = adminService.getAllRestaurants(pageable);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/api/feedbacks")
    @ResponseBody
    public ResponseEntity<PagedFeedbackResponse> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 20)); // 최대 20개로 제한
        PagedFeedbackResponse response = adminService.getAllFeedbacks(pageable);
        return ResponseEntity.ok(response);
    }

    // 모달 관리 API
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/api/modal")
    @ResponseBody
    public ResponseEntity<HomeModalResponse> getCurrentModal() {
        HomeModalResponse modal = adminService.getCurrentModal();
        return ResponseEntity.ok(modal);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/api/modal")
    @ResponseBody
    public ResponseEntity<HomeModalResponse> updateModal(@RequestBody HomeModalUpdateRequest request) {
        HomeModalResponse modal = adminService.updateModal(request);
        return ResponseEntity.ok(modal);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/api/modal")
    @ResponseBody
    public ResponseEntity<Void> deleteModal() {
        adminService.deleteModal();
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/api/modal/preview")
    @ResponseBody
    public ResponseEntity<String> previewModal(@RequestBody ModalPreviewRequest request) {
        // Thymeleaf Context 생성
        Context context = new Context();
        context.setVariable("title", request.title());
        context.setVariable("body", request.body());
        
        // expiredAt 포맷팅 (있는 경우에만)
        if (request.expiredAt() != null && !request.expiredAt().isEmpty()) {
            try {
                LocalDateTime expiredAt = LocalDateTime.parse(request.expiredAt());
                context.setVariable("expiredAt", expiredAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } catch (Exception e) {
                context.setVariable("expiredAt", request.expiredAt());
            }
        }
        
        // Fragment 렌더링
        String renderedHtml = templateEngine.process("common/modal :: previewModal(title, body, expiredAt)", context);
        
        // Bootstrap과 미리보기 스타일이 포함된 완전한 HTML 페이지 생성
        String fullHtml = String.format("""
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>모달 미리보기</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { 
                        background: rgba(0,0,0,0.5);
                        padding: 20px;
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                    }
                    .modal {
                        display: block;
                        position: static;
                    }
                    .modal-dialog {
                        max-width: 500px;
                        margin: 1.75rem auto;
                    }
                    .preview-note {
                        position: fixed;
                        top: 10px;
                        right: 10px;
                        background: #fff;
                        padding: 10px;
                        border-radius: 5px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        font-size: 12px;
                        color: #666;
                        z-index: 1060;
                    }
                </style>
            </head>
            <body>
                <div class="preview-note">미리보기 모드</div>
                %s
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>
            </html>
            """, renderedHtml);
        
        return ResponseEntity.ok(fullHtml);
    }
}
