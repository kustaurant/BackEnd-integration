package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.mock.post.FakePostReactionRepository;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.domain.PostReaction;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostReactionServiceTest {
    private PostReactionService service;
    private FakePostReactionRepository repo;

    @BeforeEach
    void setUp() {
        repo = new FakePostReactionRepository();
        service = new PostReactionService(repo);
    }

    @Test
    @DisplayName("기존 반응이 없으면 LIKE를 저장하고 지표를 갱신한다")
    void createLike_whenNone() {
        // g
        Long postId = 1L; Long userId = 10L;
        // w
        PostReactionResponse res = service.setPostReaction(postId, userId, ReactionType.LIKE);
        // t
        assertThat(res.reactionType()).isEqualTo(ReactionType.LIKE);
        assertThat(res.likeCount()).isEqualTo(1);
        assertThat(res.dislikeCount()).isEqualTo(0);
        assertThat(res.netLikes()).isEqualTo(1);
        assertThat(repo.findById(new PostReactionId(postId, userId))).isPresent();
    }

    @Test
    @DisplayName("기존 반응이 없으면 DISLIKE를 저장하고 지표를 갱신한다")
    void createDislike_whenNone() {
        // g
        Long postId = 1L; Long userId = 11L;
        // w
        PostReactionResponse res = service.setPostReaction(postId, userId, ReactionType.DISLIKE);
        // t
        assertThat(res.reactionType()).isEqualTo(ReactionType.DISLIKE);
        assertThat(res.likeCount()).isEqualTo(0);
        assertThat(res.dislikeCount()).isEqualTo(1);
        assertThat(res.netLikes()).isEqualTo(-1);
        assertThat(repo.findById(new PostReactionId(postId, userId))).isPresent();
    }

    @Test
    @DisplayName("등록된 좋아요 제거")
    void removeLike_whenNone() {
        // g
        Long postId = 1L; Long userId = 12L;
        service.setPostReaction(postId, userId, ReactionType.LIKE); // 미리 좋아요 1회
        // w
        PostReactionResponse res = service.setPostReaction(postId, userId, null);
        // t
        assertThat(res.reactionType()).isNull();
        assertThat(res.likeCount()).isEqualTo(0);
        assertThat(res.dislikeCount()).isEqualTo(0);
        assertThat(res.netLikes()).isEqualTo(0);
        assertThat(repo.findById(new PostReactionId(postId, userId))).isEmpty();
    }

    @Test
    @DisplayName("다른 반응으로 변경하면 기존 반응이 교체되고 지표가 반영된다(LIKE→DISLIKE)")
    void switchReaction_likeToDislike() {
        // g
        Long postId = 1L; Long userId = 13L;
        service.setPostReaction(postId, userId, ReactionType.LIKE); // 먼저 좋아요
        // w
        PostReactionResponse res = service.setPostReaction(postId, userId, ReactionType.DISLIKE);
        // t
        assertThat(res.reactionType()).isEqualTo(ReactionType.DISLIKE);
        assertThat(res.likeCount()).isEqualTo(0);
        assertThat(res.dislikeCount()).isEqualTo(1);
        assertThat(res.netLikes()).isEqualTo(-1);
        assertThat(repo.findById(new PostReactionId(postId, userId)))
                .isPresent()
                .get()
                .extracting(PostReaction::getReaction)
                .isEqualTo(ReactionType.DISLIKE);
    }

    @Test
    @DisplayName("여러 사용자의 반응이 누적되어 집계된다")
    void aggregatesAcrossUsers() {
        // g
        Long postId = 1L;
        // w
        service.setPostReaction(postId, 20L, ReactionType.LIKE);     // like:1
        service.setPostReaction(postId, 21L, ReactionType.DISLIKE);  // like:1, dislike:1
        PostReactionResponse res = service.setPostReaction(postId, 22L, ReactionType.LIKE); // like:2, dislike:1
        // t
        assertThat(res.likeCount()).isEqualTo(2);
        assertThat(res.dislikeCount()).isEqualTo(1);
        assertThat(res.netLikes()).isEqualTo(1);
    }

}