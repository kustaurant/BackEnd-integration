package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.exception.exception.auth.AccessDeniedException;
import com.kustaurant.kustaurant.mock.post.*;
import com.kustaurant.kustaurant.mock.user.FakeUserRepository;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReaction;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReactionId;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.domain.*;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.user.mypage.service.UserStatsService;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PostServiceTest {
    private PostService postService;
    private FakePostRepository fakePostRepository;
    private FakePostPhotoRepository fakePostPhotoRepository;
    private FakePostScrapRepository fakePostScrapRepository;
    private FakePostReactionRepository fakePostReactionRepository;
    private FakePostCommentRepository fakePostCommentRepository;
    private FakePostCommentReactionRepository fakePostCommentReactionRepository;

    private UserStatsService userStatsService;

    @BeforeEach
    void init(){
        fakePostRepository = new FakePostRepository();
        fakePostPhotoRepository = new FakePostPhotoRepository();
        fakePostScrapRepository = new FakePostScrapRepository();
        fakePostReactionRepository = new FakePostReactionRepository();
        fakePostCommentRepository = new FakePostCommentRepository();
        fakePostCommentReactionRepository = new FakePostCommentReactionRepository();
        userStatsService = mock(UserStatsService.class);
        this.postService = new PostService(fakePostRepository,fakePostPhotoRepository,fakePostScrapRepository,fakePostReactionRepository,fakePostCommentRepository,fakePostCommentReactionRepository,userStatsService);

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
    @DisplayName("PostRequest를 이용하여 게시물을 생성할 수 있다.")
    void createPostWithPostRequest(){
        //g
        PostRequest postRequest = new PostRequest("테스트 게시글2", PostCategory.FREE,"테스트 게시글 내용2");
        //w
        Post newPost = postService.create(postRequest, 1L);
        //t
        assertThat(newPost.getId()).isNotNull();
        assertThat(newPost.getTitle()).isEqualTo("테스트 게시글2");
        assertThat(newPost.getCategory()).isEqualTo(PostCategory.FREE);
        assertThat(newPost.getWriterId()).isEqualTo(1L);
        assertThat(newPost.getBody()).isEqualTo("테스트 게시글 내용2");
    }

    @Test
    @DisplayName("게시글 작성자는 PostRequest를 이용하여 자신의 게시물을 수정할 수 있다")
    void ownerCanEditPost(){
        //g
        PostRequest postRequest = new PostRequest("게시글수정", PostCategory.FREE,"하마의 요염한 자태");
        //w
        Post rewritenPost = postService.update(1L, postRequest, 1L);
        //t
        assertThat(rewritenPost.getId()).isEqualTo(1L);
        assertThat(rewritenPost.getTitle()).isEqualTo("게시글수정");
        assertThat(rewritenPost.getCategory()).isEqualTo(PostCategory.FREE);
        assertThat(rewritenPost.getBody()).isEqualTo("하마의 요염한 자태");
    }

    @Test
    @DisplayName("게시글 작성자가 아니면 게시물을 수정할 수 없다")
    void otherUserCanNotEditOthersPost(){
        //g
        PostRequest postRequest = new PostRequest("게시글수정", PostCategory.FREE,"하마의 요염한 자태");
        //w +t
        assertThatThrownBy(()->postService.update(1L, postRequest, 2L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글 작성자는 자신의 게시글을 삭제할 수 있다")
    void ownerCanDeletePost(){
        //g

        //w
        postService.delete(1L, 1L);
        //t
        assertThat(fakePostRepository.findById(1L)).isEmpty();
    }

    @Test
    @DisplayName("게시글이 삭제되면 연관된 댓글,좋아요,스크랩 이 모두 삭제된다")
    void deletePost_cascadesAllChildren(){
        //g
        PostPhoto postPhoto = new PostPhoto(1,1L,"url", PostStatus.ACTIVE);
        fakePostPhotoRepository.save(postPhoto);

        PostScrap postScrap = new PostScrap(new PostReactionId(1L,10L));
        fakePostScrapRepository.save(postScrap);

        PostReaction postReaction = new PostReaction(new PostReactionId(1L, 11L), ReactionType.LIKE);
        fakePostReactionRepository.save(postReaction);

        PostComment postComment = new PostComment(1L,"body", PostCommentStatus.ACTIVE,12L,1L,null,null,LocalDateTime.now(),LocalDateTime.now());
        fakePostCommentRepository.save(postComment);

        PostCommentReaction postCommentReaction = new PostCommentReaction(new PostCommentReactionId(1L, 12L), ReactionType.LIKE);
        fakePostCommentReactionRepository.save(postCommentReaction);
        fakePostCommentReactionRepository.indexComment(postComment.getId(), 1L); // 댓글 반응 은 postId필드가 없어서 별도 매핑

        //w
        postService.delete(1L, 1L);

        //t
        assertThat(fakePostRepository.findById(1L)).isEmpty();
        assertThat(fakePostPhotoRepository.findByPostId(1L)).isEmpty();
        assertThat(fakePostReactionRepository.findById(new PostReactionId(1L, 11L))).isEmpty();
        assertThat(fakePostCommentRepository.findById(1L)).isEmpty();
        assertThat(fakePostCommentReactionRepository.findById(new PostCommentReactionId(1L, 12L))).isEmpty();
    }

    @Test
    @DisplayName("게시글 작성자가 아니면 게시글을 삭제할 수 없다")
    void otherUserCanNotDeletePost(){
        //g
        //w+t
        assertThatThrownBy(()->postService.delete(1L, 2L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("접근 권한이 없습니다.");
    }

}