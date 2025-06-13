package com.kustaurant.kustaurant.modal.infrastructure;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "home_modal_tbl")
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