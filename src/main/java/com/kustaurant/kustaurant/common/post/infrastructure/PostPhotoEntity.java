package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostPhoto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post_photoes_tbl")
public class PostPhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer photoId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    private String photoImgUrl;
    private String status;

    public PostPhotoEntity() {
    }

    public PostPhotoEntity(String photoImgUrl, String status, PostEntity post) {
        this.photoImgUrl = photoImgUrl;
        this.status = status;
        this.post = post;
    }

    public PostPhoto toDomain() {
        return PostPhoto.builder()
                .id(photoId)
                .photoImgUrl(photoImgUrl)
                .status(status)
                .build();
    }

    public static PostPhotoEntity from(PostPhoto postPhoto, PostEntity postEntity) {
        return new PostPhotoEntity(
                postPhoto.getPhotoImgUrl(),
                postPhoto.getStatus(),
                postEntity
        );
    }
}