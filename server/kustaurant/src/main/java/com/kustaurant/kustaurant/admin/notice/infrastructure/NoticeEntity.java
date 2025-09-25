package com.kustaurant.kustaurant.admin.notice.infrastructure;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "admin_notice")
public class NoticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Integer id;

    @Column(name = "notice_title")
    String title;
    @Column(name = "notice_href")
    String href;
    String status;
    LocalDate createdAt;
    LocalDateTime updatedAt;
}