package com.kustaurant.restauranttier.tab4_community.service;


import com.kustaurant.restauranttier.tab4_community.DataNotFoundException;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab4_community.dto.PostDTO;
import com.kustaurant.restauranttier.tab4_community.repository.PostApiRepository;
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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostApiService {
    private final PostApiRepository postApiRepository;
    private final UserRepository userRepository;
    private final PostScrapApiRepository postScrapApiRepository;
    // 인기순 제한 기준 숫자
    public static  final int POPULARCOUNT = 5;
    // 페이지 숫자
    public static  final int PAGESIZE=10;

    // 메인 화면 로딩하기
    public Page<PostDTO> getList(int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (sort.isEmpty() || sort.equals("recent")) {
            sorts.add(Sort.Order.desc("createdAt"));
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            Page<Post> posts = this.postApiRepository.findByStatus("ACTIVE", pageable);
            return posts.map(PostDTO::fromEntity);  // Post 엔티티를 PostDTO로 변환
        } else {
            sorts.add(Sort.Order.desc("createdAt"));
            Specification<Post> spec = getSpecByPopularOver5();
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            Page<Post> posts = this.postApiRepository.findAll(spec, pageable);
            return posts.map(PostDTO::fromEntity);  // Post 엔티티를 PostDTO로 변환
        }
    }

    // 검색 결과 반환하기
    public Page<PostDTO> getList(int page, String sort, String kw, String postCategory) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Specification<Post> spec = search(kw, postCategory, sort);
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        Page<Post> posts = this.postApiRepository.findAll(spec, pageable);
        return posts.map(PostDTO::fromEntity);  // Post 엔티티를 PostDTO로 변환
    }



    // 드롭다운에서 카테고리가 설정된 상태에서 게시물 반환하기
    public Page<PostDTO> getListByPostCategory(String postCategory, int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        Specification<Post> spec = getSpecByCategoryAndPopularOver5(postCategory, sort);
        Page<Post> posts = this.postApiRepository.findAll(spec, pageable);
        return posts.map(PostDTO::fromEntity);  // Post 엔티티를 PostDTO로 변환
    }
    public Post getPost(Integer id) {
        Optional<Post> post = this.postApiRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException("post not found");
        }
    }

    public void create(Post post, User user) {
        post.setUser(user);
        Post savedpost = postApiRepository.save(post);
        user.getPostList().add(savedpost);
        userRepository.save(user);
    }
    // 게시물 리스트에 대한 시간 경과 리스트로 반환하는 함수.
    public List<String> getTimeAgoList(Page<PostDTO> postList) {
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
    public void increaseVisitCount(Post post) {
        int visitCount = post.getPostVisitCount();
        post.setPostVisitCount(++visitCount);
        postApiRepository.save(post);
    }

    // 게시글 좋아요
    public Map<String, Object> likeCreateOrDelete(Post post, User user) {
        List<User> likeUserList = post.getLikeUserList();
        List<User> dislikeUserList = post.getDislikeUserList();
        List<Post> likePostList = user.getLikePostList();
        List<Post> dislikePostList = user.getDislikePostList();
        Map<String, Object> status = new HashMap<>();

        //해당 post 를 이미 like 한 경우 - 제거
        if (likeUserList.contains(user)) {
            post.setLikeCount(post.getLikeCount() - 1);
            likePostList.remove(post);
            likeUserList.remove(user);
            status.put("likeDelete", true);
        }
        //해당 post를 이미 dislike 한 경우 - 제거하고 추가
        else if (dislikeUserList.contains(user)) {
            post.setLikeCount(post.getLikeCount() + 2);
            dislikeUserList.remove(user);
            dislikePostList.remove(post);
            likeUserList.add(user);
            likePostList.add(post);
            status.put("likeChanged", true);

        }
        // 처음 like 하는 경우-추가
        else {
            status.put("likeCreated", true);

            post.setLikeCount(post.getLikeCount() + 1);
            likeUserList.add(user);
            likePostList.add(post);
        }
        // 상태 반환

        postApiRepository.save(post);
        userRepository.save(user);
        return status;
    }

    // 게시글 싫어요
    public Map<String, Object> dislikeCreateOrDelete(Post post, User user) {
        List<User> likeUserList = post.getLikeUserList();
        List<User> dislikeUserList = post.getDislikeUserList();
        List<Post> likePostList = user.getLikePostList();
        List<Post> dislikePostList = user.getDislikePostList();
        Map<String, Object> status = new HashMap<>();
        //해당 post를 이미 dislike 한 경우 - 제거
        if (dislikeUserList.contains(user)) {
            post.setLikeCount(post.getLikeCount() + 1);
            dislikePostList.remove(post);
            dislikeUserList.remove(user);
            status.put("dislikeDelete", true);
        }
        //해당 post를 이미 like 한 경우 - 제거하고 추가
        else if (likeUserList.contains(user)) {
            post.setLikeCount(post.getLikeCount() - 2);

            likeUserList.remove(user);
            likePostList.remove(post);
            dislikeUserList.add(user);
            dislikePostList.add(post);
            status.put("dislikeChanged", true);
        }
        // 처음 dislike 하는 경우-추가
        else {
            post.setLikeCount(post.getLikeCount() - 1);
            dislikeUserList.add(user);
            dislikePostList.add(post);
            status.put("dislikeCreated", true);
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
                if(sort.equals("popular")){
                    likeCountPredicate = cb.greaterThanOrEqualTo(p.get("likeCount"), POPULARCOUNT);
                }else{
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

    private Specification<Post> getSpecByCategoryAndPopularOver5(String postCategory,String sort) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Predicate likeCountPredicate;

                Predicate statusPredicate = cb.equal(p.get("status"), "ACTIVE");
                Predicate categoryPredicate = cb.equal(p.get("postCategory"), postCategory);
                // 조건 추가
                if(sort.equals("popular")){
                    likeCountPredicate = cb.greaterThanOrEqualTo(p.get("likeCount"), POPULARCOUNT);
                    return cb.and(statusPredicate, likeCountPredicate, categoryPredicate);
                }else{

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


}
