package com.kustaurant.kustaurant.post.post.service.api;


import static com.kustaurant.kustaurant.global.exception.ErrorCode.POST_NOT_FOUND;

import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import com.kustaurant.kustaurant.post.post.domain.dto.PostUpdateDTO;
import com.kustaurant.kustaurant.post.post.domain.dto.UserDTO;
import com.kustaurant.kustaurant.post.post.domain.dto.PostDTO;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.service.web.PostService;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostApiService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final EvaluationRepository evaluationRepository;
    private final PostScrapRepository postScrapRepository;
    // 인기순 제한 기준 숫자
    public static final int POPULARCOUNT = 3;
    // 페이지 숫자
    public static final int PAGESIZE = 10;
    private final PostService postService;

    // 메인 화면 로딩하기
    public Page<PostDTO> getPosts(int page, String sort, String koreanCategory, String postBodyType) {
        Page<Post> posts;
        if (koreanCategory.equals("전체")) {
            List<Sort.Order> sorts = new ArrayList<>();
            if (sort.isEmpty() || sort.equals("recent")) {
                sorts.add(Sort.Order.desc("createdAt"));
                Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
                posts = this.postRepository.findByStatus(ContentStatus.ACTIVE, pageable);
            } else if (sort.equals("popular")) {
                sorts.add(Sort.Order.desc("createdAt"));
                Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
                posts = this.postRepository.findByStatusAndPopularCount(ContentStatus.ACTIVE, POPULARCOUNT, pageable);
            } else {
                throw new IllegalArgumentException("sort 파라미터 값이 올바르지 않습니다.");
            }
        } else {
            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(Sort.Order.desc("createdAt"));
            Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
            if (sort.equals("popular")) {
                posts = this.postRepository.findByStatusAndCategoryAndPopularCount(ContentStatus.ACTIVE, koreanCategory, POPULARCOUNT, pageable);
            } else {
                posts = this.postRepository.findByStatusAndCategory(ContentStatus.ACTIVE, koreanCategory, pageable);
            }
        }

        Page<PostDTO> result = posts.map(post -> {
            // TODO: PostDTO에 UserDTO 추가
            PostDTO dto = PostDTO.from(post);
            if (postBodyType.equals("text")) {
                dto.setPostBody(Jsoup.parse(dto.getPostBody()).text()); // HTML 제거 후 일반 텍스트 변환
            }
            return dto;
        });
        return result;  // Post 엔티티를 PostDTO로 변환
    }

    public Post getPost(Integer id) {
        Post post = this.postRepository.findByStatusAndPostId(ContentStatus.ACTIVE, id);
        return post;
    }

    @Transactional
    public ReactionStatus toggleLike(Integer postId, Long userId) {
        return postService.toggleLike(postId, userId).getStatus();
    }

    // post Id 유효성 검사
    public void validatePostId(Integer postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("잘못된 게시글 ID입니다.");
        }
    }

    private void deleteComments(Post post) {
        // ID 기반으로 댓글 삭제 처리
        Integer postId = post.getId();
        postCommentRepository.deleteByPostId(postId);
    }

    private void deletePhotos(Post post) {

        Integer postId = post.getId();
        List<PostPhoto> photos = postPhotoRepository.findByPostId(postId);
        if (photos != null && !photos.isEmpty()) {
            for (PostPhoto photo : photos) {
                photo.setStatus(ContentStatus.DELETED);
            }
            postPhotoRepository.saveAll(photos);
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
            throw new DataNotFoundException(POST_NOT_FOUND, "sort값이 잘못 입력되었습니다.");
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
            int evaluationCount = user.getEvalCount();
            UserDTO userDTO = UserDTO.from(user); // 필요한 정보를 UserDTO에 담음

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
            int evaluationCount = Math.toIntExact(evaluationRepository.findByUserId(user.getId())
                    .stream()
                    .filter(evaluation -> getYear(evaluation.getCreatedAt()) == year && getQuarter(evaluation.getCreatedAt()) == quarter)
                    .count());
            ;


            UserDTO userDTO = UserDTO.from(user); // 필요한 정보를 UserDTO에 담음
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
        if (postUpdateDTO.getTitle() != null) post.setTitle(postUpdateDTO.getTitle());
        if (postUpdateDTO.getPostCategory() != null) post.setCategory(postUpdateDTO.getPostCategory());
        if (postUpdateDTO.getContent() != null) post.setBody(postUpdateDTO.getContent());
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Integer postId, Long userId) {
        Post post = getPost(postId);
        
        // 권한 확인
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        // 게시글 상태를 DELETED로 변경
        post.setStatus(ContentStatus.DELETED);
        postRepository.save(post);
        
        // 관련 댓글 삭제
        deleteComments(post);
        
        // 관련 사진 삭제
        deletePhotos(post);
    }
}
