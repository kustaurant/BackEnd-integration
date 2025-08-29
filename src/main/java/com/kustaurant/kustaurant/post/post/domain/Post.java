package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.global.exception.exception.auth.AccessDeniedException;
import com.kustaurant.kustaurant.global.exception.exception.post.AlreadyDeletedException;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Post {
    private final Long id;
    private String title;
    private String body;
    private PostCategory category;
    private PostStatus status;
    private final Long writerId;
    private Integer visitCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private List<Integer> commentIds;
    private List<Integer> photoIds;
    private List<Integer> scrapIds;

    public Post update(Long postId, PostRequest req) {
        return this.toBuilder()
                .id(postId)
                .title(req.title())
                .category(req.category())
                .body(req.content())
                .build();
    }

    public void ensureWriterBy(Long userId) {
        if (!this.writerId.equals(userId))
            throw new AccessDeniedException();
    }

    public void ensureDeletable() {
        if (this.status == PostStatus.DELETED)
            throw new AlreadyDeletedException();
    }
}

