package com.kustaurant.kustaurant.admin.modal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "admin_home_modal")
public class HomeModalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "modal_id")
    private Integer id;
    @Column(name = "modal_title")
    String title;
    @Column(name = "modal_body")
    String body;
    LocalDateTime createdAt;
    LocalDateTime expiredAt;
}