package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostPhoto;
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
    private String status;

    public PostPhotoEntity() {

    }

    public PostPhoto toDomain() {
        return new PostPhoto(this.photoImgUrl, this.status);
    }

    public static PostPhotoEntity from(PostPhoto postPhoto, PostEntity postEntity) {
        return PostPhotoEntity.builder()
                .post(postEntity)
                .photoImgUrl(postPhoto.getPhotoImgUrl())
                .status(postPhoto.getStatus())
                .build();
    }
}