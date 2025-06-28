package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
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

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    private String photoImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private ContentStatus status;

    public PostPhotoEntity() {

    }

    public PostPhoto toDomain() {
        return new PostPhoto(
                this.photoId,
                this.post.getPostId(),
                this.photoImgUrl,
                this.status
        );
    }

    public static PostPhotoEntity from(PostPhoto postPhoto, PostEntity postEntity) {
        return PostPhotoEntity.builder()
                .post(postEntity)
                .photoImgUrl(postPhoto.getPhotoImgUrl())
                .status(postPhoto.getStatus())
                .build();
    }
}