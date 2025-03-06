package com.kustaurant.restauranttier.tab4_community.service;


import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.tab4_community.dto.PostUpdateDTO;
import com.kustaurant.restauranttier.tab4_community.dto.UserDTO;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab4_community.dto.PostDTO;
import com.kustaurant.restauranttier.tab4_community.entity.PostPhoto;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
import com.kustaurant.restauranttier.tab4_community.etc.PostStatus;
import com.kustaurant.restauranttier.tab4_community.repository.PostApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostCommentApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostPhotoApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostScrapApiRepository;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostApiService {
    private final PostApiRepository postApiRepository;
    private final UserRepository userRepository;
    private final PostScrapApiRepository postScrapApiRepository;
    private final PostCommentApiRepository postCommentApiRepository;
    private final PostPhotoApiRepository postPhotoApiRepository;
    // 인기순 제한 기준 숫자
    public static final int POPULARCOUNT = 3;
    // 페이지 숫자
    public static final int PAGESIZE = 10;

    // 메인 화면 로딩하기
    public Page<PostDTO> getList(int page, String sort, String koreanCategory) {
        if (koreanCategory.equals("전체")) {
            List<Sort.Order> sorts = new ArrayList<>();
            if (sort.isEmpty() || sort.equals("recent")) {
                sorts.add(Sort.Order.desc("createdAt"));
                Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
                Page<Post> posts = this.postApiRepository.findByStatus("ACTIVE", pageable);
                return posts.map(PostDTO::convertPostToPostDTO);  // Post 엔티티를 PostDTO로 변환
            } else if (sort.equals("popular")) {
                sorts.add(Sort.Order.desc("createdAt"));
                Specification<Post> spec = getSpecByPopularOver5();
                Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
                Page<Post> posts = this.postApiRepository.findAll(spec, pageable);
                return posts.map(PostDTO::convertPostToPostDTO);  // Post 엔티티를 PostDTO로 변환
            } else {
                throw new IllegalArgumentException("sort 파라미터 값이 올바르지 않습니다.");
            }
        } else {
            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(Sort.Order.desc("createdAt"));
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            Specification<Post> spec = getSpecByCategoryAndPopularOver5(koreanCategory, sort);
            //spec의 인기순으로 먼저 정렬, 그다음 pageable의 최신순으로 두번째 정렬 기준 설정
            Page<Post> posts = this.postApiRepository.findAll(spec, pageable);
            return posts.map(PostDTO::convertPostToPostDTO);  // Post 엔티티를 PostDTO로 변환
        }

    }

    // 검색 결과 반환하기
    public Page<PostDTO> getList(int page, String sort, String kw, String postCategory) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Specification<Post> spec = search(kw, postCategory, sort);
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        Page<Post> posts = this.postApiRepository.findAll(spec, pageable);
        return posts.map(PostDTO::convertPostToPostDTO);  // Post 엔티티를 PostDTO로 변환
    }

    public Post getPost(Integer id) {
        Optional<Post> post = this.postApiRepository.findByStatusAndPostId("ACTIVE", id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new OptionalNotExistException("해당 postId의 게시글을 찾을 수 없습니다.");
        }
    }

    public void create(Post post, User user) {
        post.setUser(user);
        Post savedpost = postApiRepository.save(post);
        user.getPostList().add(savedpost);
        userRepository.save(user);
    }

    // 조회수 증가
    public void increaseVisitCount(Post post) {
        int visitCount = post.getPostVisitCount();
        post.setPostVisitCount(++visitCount);
        postApiRepository.save(post);
    }

    public int likeCreateOrDelete(Post post, User user) {
        List<User> likeUserList = post.getLikeUserList();
        List<Post> likePostList = user.getLikePostList();
        int status;

        //해당 post 를 이미 like 한 경우 - 제거
        if (likeUserList.contains(user)) {
            post.setLikeCount(post.getLikeCount() - 1);
            likePostList.remove(post);
            likeUserList.remove(user);
            status = 0; // likeDeleted
        }
        // 처음 like 하는 경우 - 추가
        else {
            post.setLikeCount(post.getLikeCount() + 1);
            likeUserList.add(user);
            likePostList.add(post);
            status = 1; // likeCreated
        }

        postApiRepository.save(post);
        userRepository.save(user);
        return status;
    }


    private Specification<Post> search(String kw, String postCategory, String sort) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                //조인
                Join<Post, User> u1 = p.join("user", JoinType.LEFT);
                Join<Post, PostComment> c = p.join("postCommentList", JoinType.LEFT);
                Join<PostComment, User> u2 = c.join("user", JoinType.LEFT);
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

    private Specification<Post> getSpecByCategoryAndPopularOver5(String postCategory, String sort) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
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

    private Specification<Post> getSpecByPopularOver5() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
        Post post = getPost(postId);
        if (!post.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 게시글에 대한 권한이 없습니다.");
        }
        deleteComments(post);
        deleteScraps(post);
        deletePhotos(post);
        post.setStatus(PostStatus.DELETED.name());
        postApiRepository.save(post);
    }

    private void deleteComments(Post post) {
        List<PostComment> comments = post.getPostCommentList();
        for (PostComment comment : comments) {
            comment.setStatus("DELETED");
            for (PostComment reply : comment.getRepliesList()) {
                reply.setStatus("DELETED");
            }
            postCommentApiRepository.save(comment);
        }
    }

    private void deleteScraps(Post post) {
        List<PostScrap> scraps = post.getPostScrapList();
        postScrapApiRepository.deleteAll(scraps);
    }

    private void deletePhotos(Post post) {
        List<PostPhoto> photos = post.getPostPhotoList();
        if (photos != null) {
            postPhotoApiRepository.deleteAll(photos);
            post.setPostPhotoList(null); // 기존 사진과의 연결 해제
        }
    }

    // 랭킹 화면에서 유저 가져오기
    public List<UserDTO> getUserListforRanking(String sort) {
        if ("cumulative".equals(sort)) {
            // 누적 기준으로 유저 리스트 가져오기 (정렬된 상태)
            List<User> userList = userRepository.findUsersWithEvaluationCountDescending();
            // 유저의 평가수, 랭킹 첨부하기
            return calculateRank(userList);
        } else if ("quarterly".equals(sort)) {
            // 현재 날짜를 기준으로 연도와 분기 계산
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentQuarter = getCurrentQuarter(now);

            // 특정 분기의 평가 데이터를 기준으로 유저 리스트 가져오기
            List<User> userList = userRepository.findUsersByEvaluationCountForQuarter(currentYear, currentQuarter);
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

    private List<UserDTO> calculateRank(List<User> userList) {
        List<UserDTO> rankList = new ArrayList<>();
        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (User user : userList) {
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

    private List<UserDTO> calculateRankForQuarter(List<User> userList, int year, int quarter) {
        List<UserDTO> rankList = new ArrayList<>();

        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (User user : userList) {
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

    public void updatePost(PostUpdateDTO postUpdateDTO, Post post) {
        if (postUpdateDTO.getTitle() != null) post.setPostTitle(postUpdateDTO.getTitle());
        if (postUpdateDTO.getPostCategory() != null) post.setPostCategory(postUpdateDTO.getPostCategory());
        if (postUpdateDTO.getContent() != null) post.setPostBody(postUpdateDTO.getContent());
    }
}
