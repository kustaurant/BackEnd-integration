package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.common.post.domain.*;
import com.kustaurant.kustaurant.common.post.enums.*;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.post.service.port.*;
import com.kustaurant.kustaurant.common.user.controller.port.UserService;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostDislikeRepository postDislikeRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final ImageExtractor imageExtractor;


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
            return this.postRepository.findByStatus(ContentStatus.ACTIVE, pageable);

        }

        // 인기순 정렬하기
        else {
            sorts.add(Sort.Order.desc("createdAt"));
            Specification<PostEntity> spec = getSpecByPopularOver5();
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            return this.postRepository.findAll(spec, pageable);
        }


    }

    // 검색결과 반환
    public Page<PostDTO> getList(int page, String sort, String kw, String postCategory) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Specification<PostEntity> spec = search(kw, postCategory, sort);
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        Page<Post> postEntityPage = this.postRepository.findAll(spec, pageable);

        return postEntityPage
                .map(postEntity -> {
                    User user = userService.getActiveUserById(postEntity.getAuthorId());
                    PostDTO postDTO = PostDTO.from(postEntity, user);
                    postDTO.setUser(UserDTO.from(user));
                    return postDTO;
                });
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
    @Transactional
    public Post getPost(Integer id) {
        Optional<Post> post = this.postRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException("post not found");
        }
    }

    // 게시물 리스트에 대한 시간 경과 리스트로 반환하는 함수.
    public List<String> getTimeAgoList(Page<PostDTO> postList) {
        LocalDateTime now = LocalDateTime.now();
        return postList.stream().map(post -> {
            LocalDateTime createdAt = post.getCreatedAt();
            return timeAgo(now, createdAt);
        }).collect(Collectors.toList());

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
    @Transactional
    public void increaseVisitCount(Integer postId) {
        postRepository.increaseVisitCount(postId);
    }

    @Transactional
    public ReactionToggleResponse toggleLike(Integer postId, Integer userId) {
        boolean isLikedBefore = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDislikedBefore = postDislikeRepository.existsByUserIdAndPostId(userId, postId);

        ReactionStatus status;
        if (isLikedBefore) {
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            status = ReactionStatus.LIKE_DELETED;
        } else if (isDislikedBefore) {
            postDislikeRepository.deleteByUserIdAndPostId(userId, postId);
            postLikeRepository.save(new PostLike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.DISLIKE_TO_LIKE;
        } else {
            postLikeRepository.save(new PostLike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.LIKE_CREATED;
        }

        int likeCount = postLikeRepository.countByPostId(postId);
        int dislikeCount = postDislikeRepository.countByPostId(postId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
    }



    @Transactional
    public ReactionToggleResponse toggleDislike(Integer postId, Integer userId) {
        boolean isLikedBefore = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDislikedBefore = postDislikeRepository.existsByUserIdAndPostId(userId, postId);

        ReactionStatus status;
        if (isDislikedBefore) {
            postDislikeRepository.deleteByUserIdAndPostId(userId, postId);
            status = ReactionStatus.DISLIKE_DELETED;
        } else if (isLikedBefore) {
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            postDislikeRepository.save(new PostDislike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.LIKE_TO_DISLIKE;
        } else {
            postDislikeRepository.save(new PostDislike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.DISLIKE_CREATED;
        }

        int likeCount = postLikeRepository.countByPostId(postId);
        int dislikeCount = postDislikeRepository.countByPostId(postId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
    }



    public InteractionStatusResponse getUserInteractionStatus(Integer postId, Integer userId) {
        if (userId == null) {
            return new InteractionStatusResponse(LikeStatus.NOT_LIKED, DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
        }

        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDisliked = postDislikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isScrapped = postScrapRepository.existsByUserIdAndPostId(userId, postId);

        return new InteractionStatusResponse(isLiked ? LikeStatus.LIKED : LikeStatus.NOT_LIKED, isDisliked ? DislikeStatus.DISLIKED : DislikeStatus.NOT_DISLIKED, isScrapped ? ScrapStatus.SCRAPPED : ScrapStatus.NOT_SCRAPPED);
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
                    likeCountPredicate = cb.greaterThanOrEqualTo(p.get("netLikes"), POPULARCOUNT);
                } else {
                    likeCountPredicate = cb.greaterThanOrEqualTo(p.get("netLikes"), -1000);
                }

                // 검색 조건 결합 (카테고리 설정이 되어있을때는 검색 시 카테고리 안에서 검색을 한다).
                if (!postCategory.equals("전체")) {
                    categoryPredicate = cb.equal(p.get("postCategory"), postCategory);
                    return cb.and(statusPredicate, likeCountPredicate, categoryPredicate, cb.or(cb.like(p.get("postTitle"), "%" + kw + "%"), // 제목
                            cb.like(p.get("postBody"), "%" + kw + "%"),      // 내용
                            cb.like(u1.get("userNickname"), "%" + kw + "%")    // 글 작성자
                    ));
                }
                // 검색 조건 결합
                return cb.and(statusPredicate, likeCountPredicate, cb.or(cb.like(p.get("postTitle"), "%" + kw + "%"), // 제목
                        cb.like(p.get("postBody"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("nickname").get("value"), "%" + kw + "%")    // 글 작성자
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


    @Transactional
    public void deletePost(Integer postId) {
        Post post = postRepository.findByIdWithComments(postId).orElseThrow(() -> new DataNotFoundException("게시물이 존재하지 않습니다."));

        // 게시물 상태 변경
        post.delete();
        // 댓글 삭제
        post.getComments().forEach(comment -> {
            comment.delete();
            comment.getReplies().forEach(PostComment::delete);
        });

        // 스크랩 삭제
        postScrapRepository.deleteByPostId(postId);

        // 사진 삭제
        postPhotoRepository.deleteByPost_PostId(postId);

        // 저장
        postRepository.save(post);
    }

    @Transactional
    public Post create(String title, String category, String body, Integer userId) {
        Post post = Post.builder().title(title).category(category).body(body).status(ContentStatus.ACTIVE).netLikes(0).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).authorId(userId).build();
        Post savedPost = postRepository.save(post);

        List<String> imageUrls = imageExtractor.extract(body);

        for (String imageUrl : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(savedPost.getId())
                    .photoImgUrl(imageUrl)
                    .status(ContentStatus.ACTIVE)
                    .build());
        }
        return savedPost;
    }


    @Transactional
    public void update(Integer postId, String title, String category, String body) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        List<String> imageUrls = imageExtractor.extract(body);
        post.update(title, body, category, imageUrls);

        postPhotoRepository.deleteByPost_PostId(postId);
        postPhotoRepository.saveAll(post.getPhotos());
        postRepository.save(post);
    }

    public List<PostDTO> getDTOs(Page<Post> paging) {
        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post post : paging) {
            User user = userService.getActiveUserById(post.getAuthorId());
            PostDTO postDTO = PostDTO.from(post, user);
            postDTO.setUser(UserDTO.from(user));
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }
}
