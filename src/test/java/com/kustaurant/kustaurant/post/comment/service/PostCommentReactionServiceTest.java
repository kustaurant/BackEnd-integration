package com.kustaurant.kustaurant.post.comment.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.mock.post.FakePostCommentReactionRepository;
import com.kustaurant.kustaurant.mock.post.FakePostCommentRepository;
import com.kustaurant.kustaurant.post.comment.controller.request.PostCommentRequest;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommReactionResponse;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostCommentReactionServiceTest {
    private PostCommentReactionService service;
    private FakePostCommentRepository commentRepo;
    private FakePostCommentReactionRepository reactionRepo;

    private Long commentId;
    @BeforeEach
    void setUp() {
        commentRepo = new FakePostCommentRepository();
        reactionRepo = new FakePostCommentReactionRepository();
        service = new PostCommentReactionService(commentRepo, reactionRepo);

        PostComment root = PostComment.create(1L, new PostCommentRequest("루트 댓글", null), 1L);
        PostComment save = commentRepo.save(root);
        commentId = save.getId();
    }

    @Test
    @DisplayName("기존 반응 없음 -> LIKE 생성")
    void toggle_create_like() {
        //g
        //w
        PostCommReactionResponse res = service.setPostCommentReaction(commentId, 1L, ReactionType.LIKE);
        //t
        assertThat(res.likeCount()).isEqualTo(1);
        assertThat(res.dislikeCount()).isZero();
        assertThat(res.reactionType()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    @DisplayName("등록된 좋아요 제거")
    void toggle_same_like_then_remove() {
        // g
        service.setPostCommentReaction(commentId, 1L, ReactionType.LIKE);
        // w
        PostCommReactionResponse res = service.setPostCommentReaction(commentId, 1L, null);
        // t
        assertThat(res.likeCount()).isZero();
        assertThat(res.dislikeCount()).isZero();
        assertThat(res.reactionType()).isNull();
    }

    @Test
    @DisplayName("다른 반응으로 변경: LIKE -> DISLIKE")
    void toggle_switch_like_to_dislike() {
        service.setPostCommentReaction(commentId, 1L, ReactionType.LIKE);

        PostCommReactionResponse res = service.setPostCommentReaction(commentId, 1L, ReactionType.DISLIKE);

        assertThat(res.likeCount()).isZero();
        assertThat(res.dislikeCount()).isEqualTo(1);
        assertThat(res.reactionType()).isEqualTo(ReactionType.DISLIKE);
    }

    @Test
    @DisplayName("댓글이 없으면 DataNotFoundException")
    void toggle_comment_not_found() {
        assertThatThrownBy(() -> service.setPostCommentReaction(999L, 1L, ReactionType.LIKE))
                .isInstanceOf(DataNotFoundException.class);
    }

}