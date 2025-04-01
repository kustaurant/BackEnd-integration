package com.kustaurant.kustaurant.api.post.service;


import com.kustaurant.kustaurant.common.post.enums.LikeToggleStatus;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import com.kustaurant.kustaurant.common.post.domain.PostUpdateDTO;
import com.kustaurant.kustaurant.common.post.domain.UserDTO;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.post.domain.PostDTO;
import com.kustaurant.kustaurant.common.post.enums.PostStatus;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostApiService {
    private final PostRepository postRepository;
    private final OUserRepository userRepository;
    private final PostScrapApiRepository postScrapApiRepository;
    private final PostCommentApiRepository postCommentApiRepository;
    private final PostPhotoApiRepository postPhotoApiRepository;
    private final PostLikesJpaRepository postLikesJpaRepository;
    private final PostDislikesJpaRepository postDislikesJpaRepository;
    // 인기순 제한 기준 숫자
    public static final int POPULARCOUNT = 3;
    // 페이지 숫자
    public static final int PAGESIZE = 10;

    // 메인 화면 로딩하기
    public Page<PostDTO> getPosts(int page, String sort, String koreanCategory,String postBodyType) {
        Page<PostEntity> posts;
        if (koreanCategory.equals("전체")) {
            List<Sort.Order> sorts = new ArrayList<>();
            if (sort.isEmpty() || sort.equals("recent")) {
                sorts.add(Sort.Order.desc("createdAt"));
                Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
                posts = this.postRepository.findByStatus("ACTIVE", pageable);
            } else if (sort.equals("popular")) {
                sorts.add(Sort.Order.desc("createdAt"));
                Specification<PostEntity> spec = getSpecByPopularOver5();
                Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
                posts = this.postRepository.findAll(spec, pageable);
            } else {
                throw new IllegalArgumentException("sort 파라미터 값이 올바르지 않습니다.");
            }
        } else {
            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(Sort.Order.desc("createdAt"));
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            Specification<PostEntity> spec = getSpecByCategoryAndPopularCount(koreanCategory, sort);
            //spec의 인기순으로 먼저 정렬, 그다음 pageable의 최신순으로 두번째 정렬 기준 설정
            posts = this.postRepository.findAll(spec, pageable);
        }

        Page<PostDTO> result = posts.map(post -> {
            PostDTO dto = PostDTO.convertPostToPostDTO(post);
            if (postBodyType.equals("text")) {
                dto.setPostBody(Jsoup.parse(dto.getPostBody()).text()); // HTML 제거 후 일반 텍스트 변환
            }
            return dto;
        });
        return result;  // Post 엔티티를 PostDTO로 변환
    }

    public PostEntity getPost(Integer id) {
        Optional<PostEntity> post = this.postRepository.findByStatusAndPostId("ACTIVE", id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new OptionalNotExistException("해당 postId의 게시글을 찾을 수 없습니다.");
        }
    }

    public void create(PostEntity postEntity, UserEntity user) {
        postEntity.setUser(user);
        PostEntity savedpost = postRepository.save(postEntity);
        user.getPostList().add(savedpost);
        userRepository.save(user);
    }

    // 조회수 증가
    public void increaseVisitCount(PostEntity postEntity) {
        int visitCount = postEntity.getPostVisitCount();
        postEntity.setPostVisitCount(++visitCount);
        postRepository.save(postEntity);
    }
    @Transactional
    public LikeToggleStatus toggleLikeStatus(PostEntity postEntity, UserEntity user) {
        Optional<PostLikesEntity> likeOptional = postLikesJpaRepository.findByPostEntityAndUser(postEntity, user);

        //해당 post 를 이미 like 한 경우 - 제거
        if (likeOptional.isPresent()) {
            PostLikesEntity like = likeOptional.get();
            postLikesJpaRepository.delete(like);
            postEntity.getPostLikesList().remove(like);
            user.getPostLikesList().remove(like);
            postEntity.setLikeCount(postEntity.getLikeCount() - 1);
            return LikeToggleStatus.DELETED; // likeDeleted
        }
        // 처음 like 하는 경우 - 추가
        else {
            PostLikesEntity postLikesEntity = new PostLikesEntity(user, postEntity);
            postLikesJpaRepository.save(postLikesEntity);
            postEntity.getPostLikesList().add(postLikesEntity);
            user.getPostLikesList().add(postLikesEntity);
            postEntity.setLikeCount(postEntity.getLikeCount() + 1);
            return LikeToggleStatus.CREATED; // likeDeleted
        }
    }


    private Specification<PostEntity> search(String kw, String postCategory, String sort) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PostEntity> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                //조인
                Join<PostEntity, UserEntity> u1 = p.join("user", JoinType.LEFT);
                Join<PostEntity, PostComment> c = p.join("postCommentList", JoinType.LEFT);
                Join<PostComment, UserEntity> u2 = c.join("user", JoinType.LEFT);
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

    private Specification<PostEntity> getSpecByCategoryAndPopularCount(String postCategory, String sort) {
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

    // post Id 유효성 검사
    public void validatePostId(Integer postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("잘못된 게시글 ID입니다.");
        }
    }

    // 게시글 삭제
    public void deletePost(Integer postId, Integer userId) {
        PostEntity postEntity = getPost(postId);
        if (!postEntity.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 게시글에 대한 권한이 없습니다.");
        }
        deleteComments(postEntity);
        deleteScraps(postEntity);
        deletePhotos(postEntity);
        postEntity.setStatus(PostStatus.DELETED.name());
        postRepository.save(postEntity);
    }

    private void deleteComments(PostEntity postEntity) {
        List<PostComment> comments = postEntity.getPostCommentList();
        for (PostComment comment : comments) {
            comment.setStatus("DELETED");
            for (PostComment reply : comment.getRepliesList()) {
                reply.setStatus("DELETED");
            }
            postCommentApiRepository.save(comment);
        }
    }

    private void deleteScraps(PostEntity postEntity) {
        List<PostScrap> scraps = postEntity.getPostScrapList();
        postScrapApiRepository.deleteAll(scraps);
    }

    private void deletePhotos(PostEntity postEntity) {
        List<PostPhoto> photos = postEntity.getPostPhotoList();
        if (photos != null) {
            postPhotoApiRepository.deleteAll(photos);
            postEntity.setPostPhotoList(null); // 기존 사진과의 연결 해제
        }
    }

    // 랭킹 화면에서 유저 가져오기
    public List<UserDTO> getUserListforRanking(String sort) {
        if ("cumulative".equals(sort)) {
            // 누적 기준으로 유저 리스트 가져오기 (정렬된 상태)
            List<UserEntity> userList = userRepository.findUsersWithEvaluationCountDescending();
            // 유저의 평가수, 랭킹 첨부하기
            return calculateRank(userList);
        } else if ("quarterly".equals(sort)) {
            // 현재 날짜를 기준으로 연도와 분기 계산
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentQuarter = getCurrentQuarter(now);

            // 특정 분기의 평가 데이터를 기준으로 유저 리스트 가져오기
            List<UserEntity> userList = userRepository.findUsersByEvaluationCountForQuarter(currentYear, currentQuarter);
            // 분기별 순위 리스트 계산
            return calculateRankForQuarter(userList, currentYear, currentQuarter);
        } else {
            throw new OptionalNotExistException("sort값이 잘못 입력되었습니다.");
        }
    }

    private int getCurrentQuarter(LocalDate date) {
        int month = date.getMonthValue();
        if (month >= 1 && month <= 3) {
            return 1;
        } else if (month >= 4 && month <= 6) {
            return 2;
        } else if (month >= 7 && month <= 9) {
            return 3;
        } else {
            return 4;
        }
    }

    private List<UserDTO> calculateRank(List<UserEntity> userList) {
        List<UserDTO> rankList = new ArrayList<>();
        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (UserEntity user : userList) {
            int evaluationCount = user.getEvaluationList().size();
            UserDTO userDTO = UserDTO.convertUserToUserDTO(user); // 필요한 정보를 UserDTO에 담음

            if (evaluationCount < prevCount) {
                i += countSame;
                userDTO.setRank(i);
                countSame = 1;
            } else {
                userDTO.setRank(i);
                countSame++;
            }

            rankList.add(userDTO);
            prevCount = evaluationCount;
        }
        return rankList;
    }

    private List<UserDTO> calculateRankForQuarter(List<UserEntity> userList, int year, int quarter) {
        List<UserDTO> rankList = new ArrayList<>();

        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (UserEntity user : userList) {
            // 특정 분기의 평가 수 계산
            int evaluationCount = (int) user.getEvaluationList().stream().filter(e -> getYear(e.getCreatedAt()) == year && getQuarter(e.getCreatedAt()) == quarter).count();

            UserDTO userDTO = UserDTO.convertUserToUserDTO(user); // 필요한 정보를 UserDTO에 담음
            userDTO.setEvaluationCount(evaluationCount); // 분기 내 평가 수를 설정

            if (evaluationCount < prevCount) {
                i += countSame;
                userDTO.setRank(i);
                countSame = 1;
            } else {
                userDTO.setRank(i);
                countSame++;
            }

            rankList.add(userDTO);
            prevCount = evaluationCount;
        }
        return rankList;
    }

    private int getYear(LocalDateTime dateTime) {
        return dateTime.getYear();
    }

    private int getQuarter(LocalDateTime dateTime) {
        int month = dateTime.getMonthValue();
        if (month <= 3) {
            return 1;
        } else if (month <= 6) {
            return 2;
        } else if (month <= 9) {
            return 3;
        } else {
            return 4;
        }
    }

    public void updatePost(PostUpdateDTO postUpdateDTO, PostEntity postEntity) {
        if (postUpdateDTO.getTitle() != null) postEntity.setPostTitle(postUpdateDTO.getTitle());
        if (postUpdateDTO.getPostCategory() != null) postEntity.setPostCategory(postUpdateDTO.getPostCategory());
        if (postUpdateDTO.getContent() != null) postEntity.setPostBody(postUpdateDTO.getContent());
    }
}
