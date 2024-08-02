package com.kustaurant.restauranttier.tab4_community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="post_photoes_tbl")
public class PostPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer photoId;

    public PostPhoto(String photoImgUrl, String status) {
        this.photoImgUrl = photoImgUrl;
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name="post_id")
    Post post;

    String photoImgUrl;
    String status;

    public PostPhoto() {

    }
}
