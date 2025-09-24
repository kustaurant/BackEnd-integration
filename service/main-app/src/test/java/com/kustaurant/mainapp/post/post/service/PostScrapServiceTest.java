package com.kustaurant.mainapp.post.post.service;

import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import com.kustaurant.mainapp.mock.post.FakePostRepository;
import com.kustaurant.mainapp.mock.post.FakePostScrapRepository;
import com.kustaurant.mainapp.post.post.controller.response.PostScrapResponse;
import com.kustaurant.mainapp.post.post.domain.Post;
import com.kustaurant.mainapp.post.post.domain.PostReactionId;
import com.kustaurant.mainapp.post.post.domain.PostScrap;
import com.kustaurant.mainapp.post.post.domain.enums.PostCategory;
import com.kustaurant.mainapp.post.post.domain.enums.ScrapStatus;
import com.kustaurant.mainapp.user.mypage.service.UserStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PostScrapServiceTest {
    private PostScrapService postScrapService;
    private FakePostRepository fakePostRepository;
    private FakePostScrapRepository fakePostScrapRepository;


    private UserStatsService userStatsService;

    @BeforeEach
    void init(){
        fakePostRepository = new FakePostRepository();
        fakePostScrapRepository = new FakePostScrapRepository();
        userStatsService = mock(UserStatsService.class);
        postScrapService = new PostScrapService(fakePostScrapRepository, fakePostRepository, userStatsService);

        Post post1 = Post.builder()
                .id(1L)
                .title("테스트 게시글1")
                .category(PostCategory.FREE)
                .body("여긴 자유 게시판")
                .writerId(1L)
                .build();
        fakePostRepository.save(post1);
    }

    @Test
    @DisplayName("스크랩 첫 시도 시 스크랩 정보가 저장되고 SCRAPPED로 응답한다")
    void givenNoScrap_whenToggle_thenScrappedAndCountIs1(){
        //g
        //w
        PostScrapResponse res = postScrapService.toggleScrapWithCount(1L, 10L,true);
        //t
        assertThat(res.postScrapCount()).isEqualTo(1);
        assertThat(res.isScrapped()).isEqualTo(true);
        assertThat(fakePostScrapRepository.findById(new PostReactionId(1L, 10L))).isPresent();
    }

    @Test
    @DisplayName("이미 스크랩된 게시글을 다시 누르면 스크랩이 취소되고 NOT_SCRAPPED로 응답한다")
    void givenAlreadyScrapped_whenToggle_thenUnScrappedAndCountIs0() {
        //g 이미 스크랩을 누름
        postScrapService.toggleScrapWithCount(1L, 10L,true);
        //w 다시 토글 시도
        PostScrapResponse res = postScrapService.toggleScrapWithCount(1L, 10L,false);
        //t
        assertThat(res.postScrapCount()).isEqualTo(0);
        assertThat(res.isScrapped()).isEqualTo(false);
        assertThat(fakePostScrapRepository.findById(new PostReactionId(1L, 10L))).isEmpty();
    }

    @Test
    @DisplayName("없는 게시글에 스크랩 요청 시 POST_NOT_FOUND 예외가 발생한다")
    void givenNonexistentPost_whenToggle_thenThrowsDataNotFound() {
        //g
        //w + t
        assertThatThrownBy(() -> postScrapService.toggleScrapWithCount(999L, 10L,true))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("첫 스크랩 중 경합으로 중복키 예외가 나도 SCRAPPED로 응답한다")
    void givenDuplicateKeyOnSave_whenToggle_thenStillScrapped() {
        // g
        RaceyPostScrapRepository raceyRepo = new RaceyPostScrapRepository();
        postScrapService = new PostScrapService(raceyRepo, fakePostRepository, userStatsService);
        // w
        PostScrapResponse res = postScrapService.toggleScrapWithCount(1L, 10L,true);
        // t
        assertThat(res.isScrapped()).isEqualTo(true);
        assertThat(res.postScrapCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 유저가 같은 글을 스크랩 -> 개수 누적")
    void givenMultipleUsers_whenToggle_thenCountAccumulates() {
        //g
        //w
        postScrapService.toggleScrapWithCount(1L, 10L,true);
        postScrapService.toggleScrapWithCount(1L, 11L,true);
        PostScrapResponse res = postScrapService.toggleScrapWithCount(1L, 12L,true);
        //t
        assertThat(res.postScrapCount()).isEqualTo(3);
        assertThat(res.isScrapped()).isEqualTo(true);
    }


    static class RaceyPostScrapRepository extends FakePostScrapRepository {
        @Override
        public void save(PostScrap postScrap) {
            // 첫 저장 순간에 다른 스레드가 이미 삽입했다고 가정하고, 중복키 예외로 시뮬레이션
            throw new DataIntegrityViolationException("duplicate key");
        }
        @Override
        public int countByPostId(Long postId) {
            // "다른 스레드가 이미 넣어 뒀다"는 가정에 맞춰 1을 반환
            return 1;
        }
        @Override
        public Optional<PostScrap> findById(PostReactionId id) {
            // find 시점에는 비어 있었다고 가정(= Optional.empty())
            return Optional.empty();
        }
    }

}