package com.kustaurant.kustaurant.common.modal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "home_modal_tbl")
public class HomeModal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer modalId;

    String modalTitle;
    String modalBody;
    LocalDateTime createdAt;
    LocalDateTime expiredAt;
}