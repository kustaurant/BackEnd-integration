package com.kustaurant.restauranttier.tab1_home.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notice_tbl")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeId;

    String noticeTitle;
    String noticeHref;
    String status;
    LocalDate createdAt;
    LocalDateTime updatedAt;
}