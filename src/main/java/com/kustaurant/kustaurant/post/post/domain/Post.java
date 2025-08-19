package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.global.exception.exception.post.AlreadyDeletedException;
import com.kustaurant.kustaurant.global.exception.exception.post.NoDeleteAuthorityException;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Post {
    private final Integer id;
    private final String title;
    private final String body;
    private final PostCategory category;
    private PostStatus status;
    private final Long writerId;
    private final Integer visitCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private List<Integer> commentIds;
    private List<Integer> photoIds;
    private List<Integer> scrapIds;

    public Post update(PostRequest req) {
        return Post.builder()
                .title(req.title())
                .body(req.content())
                .category(req.category())
                .build();
    }

    public void ensureWriterBy(Long userId) {
        if (!this.writerId.equals(userId))
            throw new NoDeleteAuthorityException();
    }

    public void ensureDeletable() {
        if (this.status == PostStatus.DELETED)
            throw new AlreadyDeletedException();
    }
}

