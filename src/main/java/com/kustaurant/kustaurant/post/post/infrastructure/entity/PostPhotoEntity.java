package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "post_photoes_tbl")
public class PostPhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer photoId;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    private String photoImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private PostStatus status;

    public PostPhotoEntity() {

    }

    public PostPhoto toDomain() {
        return new PostPhoto(
                this.photoId,
                this.postId,
                this.photoImgUrl,
                this.status
        );
    }

    public static PostPhotoEntity from(PostPhoto postPhoto) {
        return PostPhotoEntity.builder()
                .postId(postPhoto.getPostId())
                .photoImgUrl(postPhoto.getPhotoImgUrl())
                .status(postPhoto.getStatus())
                .build();
    }
}