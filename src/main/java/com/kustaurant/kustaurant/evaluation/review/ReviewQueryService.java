package com.kustaurant.kustaurant.evaluation.review;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.kustaurant.evaluation.comment.service.port.EvalCommentRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.EvalUserReactionRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {
    private final EvaluationQueryRepository evalQueryRepo;
    private final EvalCommentRepository evalCommentRepo;
    private final EvalUserReactionRepository evalReactionRepo;
    private final EvalCommUserReactionRepository evalCommReactionRepo;
    private final UserRepository userRepo;

    public List<ReviewsResponse> fetchEvaluationsWithComments(
            Integer restaurantId,
            Long currentUserId,
            SortOption sort
    ) {

        /* 2) 평가(리뷰) 목록 – 작성자·댓글을 fetch-join 하지 않는다 */
        List<Evaluation> evals = switch (sort) {
            case LATEST     -> evalQueryRepo.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
            case POPULARITY -> evalQueryRepo.findByRestaurantIdOrderByLikeCountDesc(restaurantId);
        };

        if (evals.isEmpty()) return List.of();

        /* 3) 한 번에 필요한 ID 묶기 */
        List<Long> evalIds   = evals.stream().map(Evaluation::getId).toList();
        List<Long> writerIds = evals.stream().map(Evaluation::getUserId).toList();

        /* 4) 평가별 댓글들 한 방 조회 후 grouping */
        Map<Long, List<EvalComment>> commentMap = evalCommentRepo
                .findAllByEvaluationIdIn(evalIds)
                .stream()
                .collect(Collectors.groupingBy(EvalComment::getEvaluationId));

        /* 5) 작성자 프로필 batch 로딩 */
        Set<Long> allUserIds = new HashSet<>(writerIds);
        commentMap.values().stream()
                .flatMap(List::stream)
                .forEach(c -> allUserIds.add(c.getUserId()));

        Map<Long, User> userMap = userRepo.findByIdIn(new ArrayList<>(allUserIds)).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        /* 6) “내가 평가에 누른 리액션” 한 방 조회 */
        Map<Long, ReactionType> myEvalReact =
                (currentUserId == null) ? Map.of()
                        : evalReactionRepo.toMap(currentUserId, evalIds);

        /* 7) “내가 댓글에 누른 리액션” 한 방 조회 */
        Map<Long, ReactionType> myCommReact;
        {
            List<Long> commentIds = commentMap.values().stream()
                    .flatMap(List::stream)
                    .map(EvalComment::getId)
                    .toList();

            myCommReact = (currentUserId == null || commentIds.isEmpty()) ? Map.of()
                    : evalCommReactionRepo.toMap(currentUserId, commentIds);
        }

        /* 8) DTO 매핑 */
        return evals.stream()
                .map(ev -> {
                    User writer = userMap.get(ev.getUserId());

                    /* 댓글 DTO 리스트 */
                    List<EvalCommentResponse> commentDtos = commentMap
                            .getOrDefault(ev.getId(), List.of())
                            .stream()
                            .map(c -> EvalCommentResponse.from(
                                    c,
                                    writer,                      // 댓글 작성자도 동일
                                    myCommReact.get(c.getId()),  // 내 댓글 리액션
                                    currentUserId == null ? -1L : currentUserId
                            ))
                            .toList();

                    return new ReviewsResponse(
                            ev.getId(),                                  // evalId
                            ev.getEvaluationScore(),                               // evalScore
                            UserIconResolver.resolve(writer.getEvalCount()),
                            writer.getNickname().toString(),
                            TimeAgoUtil.toKor(ev.getCreatedAt()),
                            ev.getCommentImgUrl(),                              // evalImgUrl
                            ev.getCommentBody(),                                // evalBody
                            myEvalReact.get(ev.getId()),                 // reactionType (평가)
                            ev.getLikeCount(),                           // evalLikeCount
                            ev.getDislikeCount(),                        // evalDislikeCount
                            currentUserId != null && currentUserId.equals(writer.getId()),
                            commentDtos,                                 // evalCommentList
                            writer,                                      // @JsonIgnore
                            ev                                           // @JsonIgnore
                    );
                })
                .toList();
    }
}
