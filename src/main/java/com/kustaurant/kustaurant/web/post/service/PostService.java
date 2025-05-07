package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.common.post.domain.InteractionStatusResponse;
import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostDislike;
import com.kustaurant.kustaurant.common.post.domain.PostLike;
import com.kustaurant.kustaurant.common.post.enums.*;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.post.service.port.PostDislikeRepository;
import com.kustaurant.kustaurant.common.post.service.port.PostLikeRepository;
import com.kustaurant.kustaurant.common.post.service.port.PostRepository;
import com.kustaurant.kustaurant.common.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.common.user.controller.port.UserService;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostDislikeRepository postDislikeRepository;

    // 인기순 제한 기준 숫자
    public static final int POPULARCOUNT = 3;
    // 페이지 숫자
    public static final int PAGESIZE = 10;
    private final UserService userService;

    // 메인 화면 로딩하기
    public Page<Post> getList(int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        // 최신순 정렬
        if (sort.isEmpty() || sort.equals("recent")) {
            sorts.add(Sort.Order.desc("createdAt"));
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            return this.postRepository.findByStatus("ACTIVE", pageable);

        }

        // 인기순 정렬하기
        else {
            sorts.add(Sort.Order.desc("createdAt"));
            Specification<PostEntity> spec = getSpecByPopularOver5();
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            return this.postRepository.findAll(spec, pageable);
        }


    }

    // 검색 결과 반환하기
    public Page<Post> getList(int page, String sort, String kw, String postCategory) {
        List<Sort.Order> sorts = new ArrayList<>();

        // 인기순 최신순 모두 최신순으로
        sorts.add(Sort.Order.desc("createdAt"));

        Specification<PostEntity> spec = search(kw, postCategory, sort);

        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        return this.postRepository.findAll(spec, pageable);
    }


    //  드롭다운에서 카테고리가 설정된 상태에서 게시물 반환하기
    public Page<Post> getListByPostCategory(String postCategory, int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();

        // 인기순 최신순 모두 최신순으로
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        Specification<PostEntity> spec = getSpecByCategoryAndPopularOver5(postCategory, sort);
        return this.postRepository.findAll(spec, pageable);

    }

    public Post getPost(Integer id) {
        Optional<Post> post = this.postRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException("post not found");
        }
    }

    //    @Transactional
//    public void create(PostEntity postEntity, UserEntity user) {
//        postEntity.setUser(user);
//        PostEntity savedpost = postRepository.save(postEntity);
//        userRepository.save(user);
//    }
    // 게시물 리스트에 대한 시간 경과 리스트로 반환하는 함수.
    public List<String> getTimeAgoList(Page<PostEntity> postList) {
        LocalDateTime now = LocalDateTime.now();

        // postList의 createdAt 필드를 문자열 형식으로 만들어 timeAgoList에 할당
        List<String> timeAgoList = postList.stream()
                .map(post -> {
                    LocalDateTime createdAt = post.getCreatedAt();
                    // datetime 타입의 createdAt을 string 타입으로 변환해주는 함수
                    return timeAgo(now, createdAt);
                })
                .collect(Collectors.toList());
        return timeAgoList;

    }

    // 작성글이나 댓글이 만들어진지 얼마나 됐는지 계산하는 함수
    public String timeAgo(LocalDateTime now, LocalDateTime past) {
        long minutes = Duration.between(past, now).toMinutes();
        if (minutes < 60) {
            return minutes + "분 전";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "시간 전";
        }
        long days = hours / 24;
        return days + "일 전";
    }

    // 조회수 증가
//    @Transactional
//    public void increaseVisitCount(Post post, UserEntity user) {
//        int visitCount = post.getVisitCount();
//
//        PostEntity postEntity = PostEntity.from(post, user);
//        postEntity.setPostVisitCount(++visitCount);
//    }

    @Transactional
    public Map<String, Object> likeCreateOrDelete(Integer postId, String providerId) {
        Post post = getPost(postId);
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다"));

        ReactionStatus result = post.toggleLike(user, LocalDateTime.now());

        syncLikeState(post, user, result);

        return Map.of(result.name(), true);
    }

    private void syncLikeState(Post post, User user, ReactionStatus result) {
        switch (result) {
            case LIKE_DELETED -> {
                postLikeRepository.deleteByUserAndPost(user, post);
            }
            case DISLIKE_TO_LIKE -> {
                postDislikeRepository.deleteByUserAndPost(user, post);
                postLikeRepository.save(new PostLike(user, post, LocalDateTime.now()));
            }
            case LIKE_CREATED -> {
                postLikeRepository.save(new PostLike(user, post, LocalDateTime.now()));
            }
        }

        postRepository.save(post);
    }


    // 게시글 싫어요
    @Transactional
    public Map<String, Object> dislikeCreateOrDelete(Integer postId, String providerId) {
        Map<String, Object> status = new HashMap<>();
        Post post = getPost(postId);
        User user = userRepository.findByProviderId(providerId).orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다"));
        ReactionStatus result = post.toggleDislike(user, LocalDateTime.now());

        syncDislikeState(post, user, result);

        return Map.of(result.name(), true);
    }

    private void syncDislikeState(Post post, User user, ReactionStatus result) {
        switch (result) {
            case DISLIKE_DELETED -> {
                postDislikeRepository.deleteByUserAndPost(user, post);
            }
            case LIKE_TO_DISLIKE -> {
                postLikeRepository.deleteByUserAndPost(user, post);
                postDislikeRepository.save(new PostDislike(user, post, LocalDateTime.now()));
            }
            case DISLIKE_CREATED -> {
                postDislikeRepository.save(new PostDislike(user, post, LocalDateTime.now()));
            }
        }

        postRepository.save(post);
    }


    public InteractionStatusResponse getUserInteractionStatus(Integer postId, String userName) {
        Post post = getPost(postId);
        if (userName == null) {
            return new InteractionStatusResponse(LikeStatus.NOT_LIKED, DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
        }
        User user = userRepository.findByProviderId(userName).orElse(null);

        boolean isLiked = postLikeRepository.existsByUserAndPost(user, post);
        boolean isDisliked = postDislikeRepository.existsByUserAndPost(user, post);
        boolean isScrapped = postScrapRepository.existsByUserAndPost(user, post);

        return new InteractionStatusResponse(
                isLiked ? LikeStatus.LIKED : LikeStatus.NOT_LIKED,
                isDisliked ? DislikeStatus.DISLIKED : DislikeStatus.NOT_DISLIKED,
                isScrapped ? ScrapStatus.SCRAPPED : ScrapStatus.NOT_SCRAPPED
        );
    }


    private Specification<PostEntity> search(String kw, String postCategory, String sort) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PostEntity> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                //조인
                Join<PostEntity, UserEntity> u1 = p.join("user", JoinType.LEFT);
                Join<PostEntity, PostCommentEntity> c = p.join("postCommentList", JoinType.LEFT);
                Join<PostCommentEntity, UserEntity> u2 = c.join("user", JoinType.LEFT);
                // 액티브 조건 추가
                Predicate statusPredicate = cb.equal(p.get("status"), "ACTIVE");
                Predicate categoryPredicate;
                // sort
                Predicate likeCountPredicate;
                if (sort.equals("popular")) {
                    likeCountPredicate = cb.greaterThanOrEqualTo(p.get("likeCount"), POPULARCOUNT);
                } else {
                    likeCountPredicate = cb.greaterThanOrEqualTo(p.get("likeCount"), -1000);
                }

                // 검색 조건 결합 (카테고리 설정이 되어있을때는 검색 시 카테고리 안에서 검색을 한다).
                if (!postCategory.equals("전체")) {
                    categoryPredicate = cb.equal(p.get("postCategory"), postCategory);
                    return cb.and(statusPredicate, likeCountPredicate, categoryPredicate, cb.or(cb.like(p.get("postTitle"), "%" + kw + "%"), // 제목
                            cb.like(p.get("postBody"), "%" + kw + "%"),      // 내용
                            cb.like(u1.get("userNickname"), "%" + kw + "%")    // 글 작성자
//                            ,cb.like(c.get("commentBody"), "%" + kw + "%"),      // 댓글 내용
//                            cb.like(u2.get("userNickname"), "%" + kw + "%") // 댓글 작성자
                    ));
                }
                // 검색 조건 결합
                return cb.and(statusPredicate, likeCountPredicate, cb.or(cb.like(p.get("postTitle"), "%" + kw + "%"), // 제목
                        cb.like(p.get("postBody"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("userNickname"), "%" + kw + "%")    // 글 작성자
//                        ,cb.like(c.get("commentBody"), "%" + kw + "%"),      // 댓글 내용
//                        cb.like(u2.get("userNickname"), "%" + kw + "%") // 댓글 작성자
                ));
            }
        };
    }

    private Specification<PostEntity> getSpecByCategoryAndPopularOver5(String postCategory, String sort) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PostEntity> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Predicate likeCountPredicate;

                Predicate statusPredicate = cb.equal(p.get("status"), "ACTIVE");
                Predicate categoryPredicate = cb.equal(p.get("postCategory"), postCategory);
                // 조건 추가
                if (sort.equals("popular")) {
                    likeCountPredicate = cb.greaterThanOrEqualTo(p.get("likeCount"), POPULARCOUNT);
                    return cb.and(statusPredicate, likeCountPredicate, categoryPredicate);
                } else {
                    return cb.and(statusPredicate, categoryPredicate);
                }


            }


        };
    }

    private Specification<PostEntity> getSpecByPopularOver5() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PostEntity> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Predicate statusPredicate = cb.equal(p.get("status"), "ACTIVE");
                Predicate likeCountPredicate = cb.greaterThanOrEqualTo(p.get("likeCount"), POPULARCOUNT);
                return cb.and(statusPredicate, likeCountPredicate     // 글 작성자
                );


            }


        };
    }


}
