package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.controller.response.PostDTO;
import com.kustaurant.kustaurant.post.post.controller.response.UserDTO;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostQueryRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostQueryApiService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final PostQueryRepository postQueryDAO;

    public static final int POPULARCOUNT = 3;  // 인기순 제한 기준 숫자
    public static final int PAGESIZE = 10;  // 페이지 숫자

    // 메인 화면 로딩하기
    public Page<PostDTO> getPosts(int page, SortOption sort, PostCategory category, String postBodyType) {
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        boolean isPopular = (sort == SortOption.POPULARITY);
        boolean isAll     = (category == PostCategory.ALL);

        Page<PostDTO> result;
        if (isAll && !isPopular) {       // 전체 + 최신
            result = postQueryDAO.findPostsWithAllData(pageable, null).map(PostDTO::from);
        } else if (isAll) {              // 전체 + 인기
            result = postQueryDAO.findPopularPostsWithAllData(pageable, null, POPULARCOUNT).map(PostDTO::from);
        } else if (isPopular) {          // 특정카테고리 + 인기
            result = postQueryDAO.findPopularPostsByCategoryWithAllData(category, pageable, null, POPULARCOUNT).map(PostDTO::from);
        } else {                         // 특정카테고리 + 최신
            result = postQueryDAO.findPostsByCategoryWithAllData(category, pageable, null).map(PostDTO::from);
        }

        // 텍스트 변환 처리
        if (postBodyType.equals("text")) {
            result = result.map(dto -> {
                dto.setPostBody(Jsoup.parse(dto.getPostBody()).text()); // HTML 제거 후 일반 텍스트 변환
                return dto;
            });
        }
        
        return result;
    }

    public Post getPost(Integer id) {
        return this.postRepository.findByStatusAndPostId(PostStatus.ACTIVE, id);
    }

    // post Id 유효성 검사
    public void validatePostId(Integer postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("잘못된 게시글 ID입니다.");
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

    /**
     * PostQueryDAO를 활용한 최적화된 게시글 목록 조회 (API용)
     * 모든 관련 데이터를 단일 쿼리로 조회하여 N+1 문제 해결
     */
    public Page<PostDTO> getPostsOptimized(int page, String sort, String koreanCategory, String postBodyType, Long currentUserId) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));

        Page<PostDTO> result;

        if (koreanCategory.equals("전체")) {
            if (sort.isEmpty() || sort.equals("recent")) {
                result = postQueryDAO.findPostsWithAllData(pageable, currentUserId)
                        .map(PostDTO::from);
            } else if (sort.equals("popular")) {
                result = postQueryDAO.findPopularPostsWithAllData(pageable, currentUserId, POPULARCOUNT)
                        .map(PostDTO::from);
            } else {
                throw new IllegalArgumentException("sort 파라미터 값이 올바르지 않습니다.");
            }
        } else {
            PostCategory category = convertStringToPostCategory(koreanCategory);
            if (sort.equals("popular")) {
                result = postQueryDAO.findPopularPostsByCategoryWithAllData(category, pageable, currentUserId, POPULARCOUNT)
                        .map(PostDTO::from);
            } else {
                result = postQueryDAO.findPostsByCategoryWithAllData(category, pageable, currentUserId)
                        .map(PostDTO::from);
            }
        }

        // 텍스트 변환 처리
        if (postBodyType.equals("text")) {
            result = result.map(dto -> {
                dto.setPostBody(Jsoup.parse(dto.getPostBody()).text()); // HTML 제거 후 일반 텍스트 변환
                return dto;
            });
        }

        return result;
    }

    /**
     * 한국어 카테고리 문자열을 PostCategory enum으로 변환
     */
    private PostCategory convertStringToPostCategory(String koreanCategory) {
        return switch (koreanCategory) {
            case "자유게시판" -> PostCategory.FREE;
            case "칼럼게시판" -> PostCategory.COLUMN;
            case "건의게시판" -> PostCategory.SUGGESTION;
            default -> throw new IllegalArgumentException("지원하지 않는 카테고리입니다: " + koreanCategory);
        };
    }
}