package com.kustaurant.mainapp.post.comment.service;

import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import com.kustaurant.mainapp.global.exception.exception.auth.AccessDeniedException;
import com.kustaurant.mainapp.mock.post.FakePostCommentRepository;
import com.kustaurant.mainapp.mock.post.FakePostRepository;
import com.kustaurant.mainapp.post.comment.controller.request.PostCommentRequest;
import com.kustaurant.mainapp.post.comment.controller.response.PostCommentDeleteResponse;
import com.kustaurant.mainapp.post.comment.domain.PostComment;
import com.kustaurant.mainapp.post.comment.domain.PostCommentStatus;
import com.kustaurant.mainapp.post.post.domain.Post;
import com.kustaurant.mainapp.post.post.domain.enums.PostCategory;
import com.kustaurant.mainapp.user.mypage.service.UserStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PostCommentServiceTest {
    private PostCommentService service;
    private FakePostCommentRepository fakePostCommentRepository;
    private UserStatsService userStatsService;

    @BeforeEach
    void init(){
        FakePostRepository fakePostRepository = new FakePostRepository();
        fakePostCommentRepository = new FakePostCommentRepository();
        userStatsService = mock(UserStatsService.class);
        this.service = new PostCommentService(fakePostCommentRepository, fakePostRepository, userStatsService);

        Post post1 = Post.builder()
                .id(1L)
                .title("테스트 게시글1")
                .category(PostCategory.SUGGESTION)
                .body("테스트 게시글 내용입니다")
                .writerId(1L)
                .build();
        fakePostRepository.save(post1);
    }

    @Test
    @DisplayName("루트 댓글 생성 성공 시, 저장되고 stats 증가가 호출된다")
    void create_root_comment_success() {
        // given
        PostCommentRequest req = new PostCommentRequest("루트 댓글입니다.", null);

        // when
        PostComment created = service.create(1L, req, 1L);

        // then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getPostId()).isEqualTo(1L);
        assertThat(created.getWriterId()).isEqualTo(1L);
        assertThat(created.getBody()).isEqualTo("루트 댓글입니다.");
        assertThat(created.getStatus()).isEqualTo(PostCommentStatus.ACTIVE);

        verify(userStatsService, times(1)).incPostComment(1L);
    }

    @Test
    @DisplayName("부모 댓글이 존재하면 대댓글 생성이 된다")
    void create_reply_comment_success() {
        // given: 부모 댓글 하나 생성
        PostComment parent = fakePostCommentRepository.save(PostComment.create(1L, new PostCommentRequest("부모", null), 1L));
        PostCommentRequest req = new PostCommentRequest("대댓글 본문", parent.getId());

        // when
        PostComment reply = service.create(1L, req, 1L);

        // then
        assertThat(reply.getId()).isNotNull();
        assertThat(reply.getParentCommentId()).isEqualTo(parent.getId());
        assertThat(reply.isReplyComment()).isTrue();

        verify(userStatsService, times(1)).incPostComment(1L);
    }

    @Test
    @DisplayName("대댓글 생성시 부모 댓글이 존재하지 않으면 생성 실패(DataNotFoundException)하며 stats는 호출 안 됨")
    void create_reply_comment_parent_not_found() {
        // given
        Long notExistParent = 999L;
        PostCommentRequest req = new PostCommentRequest("대댓글", notExistParent);

        // when + then
        assertThatThrownBy(() -> service.create(1L, req, 1L))
                .isInstanceOf(DataNotFoundException.class);

        verify(userStatsService, never()).incPostComment(anyLong());
    }

    @Test
    @DisplayName("부모 댓글에 활성 대댓글이 있으면 부모는 PENDING 처리되고 삭제되지 않는다")
    void delete_parent_comment_with_active_replies_becomes_pending() {
        // given: 부모 + 활성 대댓글 1개
        PostComment parent = fakePostCommentRepository.save(PostComment.create(1L, new PostCommentRequest("부모", null), 1L));
        fakePostCommentRepository.save(PostComment.create(1L, new PostCommentRequest("대댓글", parent.getId()), 1L));

        // when
        PostCommentDeleteResponse res = service.delete(parent.getId(), 1L);

        // then
        assertThat(res.status()).isEqualTo(PostCommentStatus.PENDING);
        assertThat(fakePostCommentRepository.findById(parent.getId())).isPresent(); // 삭제되지 않음
        assertThat(fakePostCommentRepository.findById(parent.getId()).get().getStatus())
                .isEqualTo(PostCommentStatus.PENDING);

        verify(userStatsService, times(1)).decPostComment(1L);
    }

    @Test
    @DisplayName("부모 댓글에 활성 대댓글이 없으면 부모 댓글은 바로 삭제된다")
    void delete_parent_comment_without_active_replies_is_deleted() {
        // given: 부모만 존재
        PostComment parent = fakePostCommentRepository.save(PostComment.create(1L, new PostCommentRequest("부모", null), 1L));

        // when
        PostCommentDeleteResponse res = service.delete(parent.getId(), 1L);

        // then
        assertThat(res.status()).isEqualTo(PostCommentStatus.DELETED);
        assertThat(res.removeIds()).contains(parent.getId());
        assertThat(fakePostCommentRepository.findById(parent.getId())).isEmpty();

        verify(userStatsService, times(1)).decPostComment(1L);
    }

    @Test
    @DisplayName("대댓글 삭제 시 즉시 삭제되고, 부모가 PENDING이고 활성 대댓글이 없으면 부모도 함께 삭제된다")
    void delete_reply_comment_and_maybe_delete_pending_parent() {
        // given: 부모를 PENDING으로 만들어놓고 대댓글 1개 달아둠
        PostComment parent = fakePostCommentRepository.save(PostComment.create(1L, new PostCommentRequest("부모", null), 1L));
        parent.pendingDelete();
        fakePostCommentRepository.save(parent);

        PostComment reply = fakePostCommentRepository.save(PostComment.create(1L, new PostCommentRequest("대댓글", parent.getId()), 1L));

        // when
        PostCommentDeleteResponse res = service.delete(reply.getId(), 1L);

        // then
        assertThat(res.status()).isEqualTo(PostCommentStatus.DELETED);
        assertThat(res.removeIds()).contains(reply.getId());
        assertThat(fakePostCommentRepository.findById(parent.getId())).isEmpty();

        verify(userStatsService, times(1)).decPostComment(1L);
    }

    @Test
    @DisplayName("작성자가 아니면 댓글을 삭제할 수 없다")
    void delete_comment_by_non_owner_forbidden() {
        // given
        PostComment c = fakePostCommentRepository.save(PostComment.create(1L, new PostCommentRequest("남의 댓글", null), 1L));

        // when + then
        assertThatThrownBy(() -> service.delete(c.getId(), 2L))
                .hasMessageContaining("접근 권한")
                .isInstanceOf(AccessDeniedException.class);

        verify(userStatsService, never()).decPostComment(anyLong());
    }

    @Test
    @DisplayName("댓글이 존재하지 않으면 삭제 시 DataNotFoundException 발생")
    void delete_comment_not_found() {
        assertThatThrownBy(() -> service.delete(999L, 1L))
                .isInstanceOf(DataNotFoundException.class);

        verify(userStatsService, never()).decPostComment(anyLong());
    }

}