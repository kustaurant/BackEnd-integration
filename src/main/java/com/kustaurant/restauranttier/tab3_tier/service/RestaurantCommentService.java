package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.common.exception.exception.ParamException;
import com.kustaurant.restauranttier.tab3_tier.dto.EvaluationDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantCommentDTO;
import com.kustaurant.restauranttier.tab3_tier.entity.*;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantCommentDislikeRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantCommentLikeRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantCommentRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantRepository;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import com.kustaurant.restauranttier.common.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RestaurantCommentService {

    private final RestaurantCommentRepository restaurantCommentRepository;
    private final RestaurantCommentLikeRepository restaurantCommentLikeRepository;
    private final RestaurantCommentDislikeRepository restaurantCommentDislikeRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public RestaurantComment findCommentByEvaluationId(Integer evaluationId) {
        return restaurantCommentRepository.findByParentEvaluationIdAndStatus(evaluationId, "ACTIVE").orElse(null);
    }

    public List<RestaurantComment> findCommentsByParentCommentId(Integer parentCommentId) {
        return restaurantCommentRepository.findByParentCommentIdAndStatus(parentCommentId, "ACTIVE");
    }

    public RestaurantComment findCommentByCommentId(Integer commentId) {
        Optional<RestaurantComment> commentOptional = restaurantCommentRepository.findByCommentIdAndStatus(commentId, "ACTIVE");
        if (commentOptional.isEmpty()) {
            throw new OptionalNotExistException(commentId + " 코멘트가 없습니다.");
        }
        return commentOptional.get();
    }

    public void createRestaurantComment(User user, Restaurant restaurant, Evaluation evaluation, EvaluationDTO evaluationDTO) {
        String commentImgUrl = null;
        if (evaluationDTO.getNewImage() != null && !evaluationDTO.getNewImage().isEmpty()) {
            commentImgUrl = s3Service.uploadFile(evaluationDTO.getNewImage());
        }
        RestaurantComment newComment = new RestaurantComment(
                user, restaurant, evaluationDTO.getEvaluationComment(), commentImgUrl, evaluation.getEvaluationId(),
                0, "ACTIVE", LocalDateTime.now()
        );
        restaurantCommentRepository.save(newComment);
    }

    public void updateRestaurantComment(EvaluationDTO evaluationDTO, RestaurantComment comment) {
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setCommentBody(evaluationDTO.getEvaluationComment());
        if (evaluationDTO.getNewImage() != null && !evaluationDTO.getNewImage().isEmpty()) {
            String commentImgUrl = s3Service.uploadFile(evaluationDTO.getNewImage());
            s3Service.deleteFile(comment.getCommentImgUrl());
            comment.setCommentImgUrl(commentImgUrl);
        }
        restaurantCommentRepository.save(comment);
    }

    // 식당 코멘트 삭제
    @Transactional
    public void deleteComment(RestaurantComment comment, User user) {
        if (comment == null || user == null) {
            return;
        }

        if (user != null && !comment.getUser().equals(user)) {
            throw new ParamException("해당 유저가 단 댓글이 아닙니다.");
        }

        List<RestaurantComment> childrenComments = findCommentsByParentCommentId(comment.getCommentId());
        if (childrenComments != null && !childrenComments.isEmpty()) {
            childrenComments.forEach(child -> {
                child.setStatus("DELETED");
                restaurantCommentRepository.save(child);
            });
        }

        comment.setStatus("DELETED");
        restaurantCommentRepository.save(comment);
    }

    public List<RestaurantCommentDTO> getRestaurantCommentList(Restaurant restaurant, User user, boolean sortPopular, String userAgent) {
        // 평가 데이터 및 댓글 가져오기
        List<RestaurantCommentDTO> mainCommentList = new ArrayList<>(restaurant.getEvaluationList().stream()
                .map(evaluation -> {
                    RestaurantComment comment = findCommentByEvaluationId(evaluation.getEvaluationId());
                    return comment != null ? new AbstractMap.SimpleEntry<>(evaluation, comment) : null;
                })
                .filter(Objects::nonNull)
                .map(entry -> RestaurantCommentDTO.convertComment(
                        entry.getValue(),          // comment
                        entry.getKey().getEvaluationScore(), // evaluationScore
                        user,
                        userAgent
                ))
                .toList());

        // 정렬
        if (sortPopular) {
            mainCommentList.sort(Comparator.comparing(RestaurantCommentDTO::getCommentLikeCount).reversed());
        } else {
            // TODO: 평가 정렬을 UpdateAt을 반영해야됨
            mainCommentList.sort(Comparator.comparing(RestaurantCommentDTO::getDate).reversed());
        }

        // 각 댓글에 대댓글 추가해서 반환
        return mainCommentList.stream()
                .peek(mainComment ->
                        mainComment.setCommentReplies(
                                findCommentsByParentCommentId(mainComment.getCommentId()).stream()
                                        .map(comment -> RestaurantCommentDTO.convertComment(comment, null, user, userAgent))
                                        .sorted(Comparator.comparing(RestaurantCommentDTO::getDate))
                                        .collect(Collectors.toList())
                        )
                )
                .toList();
    }

    public RestaurantCommentDTO getRestaurantCommentDTO(int commentId, Double score, User user, String userAgent) {
        Optional<RestaurantComment> restaurantCommentOptional = restaurantCommentRepository.findByCommentId(commentId);
        if (restaurantCommentOptional.isPresent()) {
            RestaurantComment restaurantComment = restaurantCommentOptional.get();
            RestaurantCommentDTO restaurantCommentDTO = RestaurantCommentDTO.convertComment(restaurantComment, score, user, userAgent);
            restaurantCommentDTO.setCommentReplies(
                    findCommentsByParentCommentId(restaurantCommentDTO.getCommentId()).stream()
                            .map(comment -> RestaurantCommentDTO.convertComment(comment, null, user, userAgent))
                            .sorted(Comparator.comparing(RestaurantCommentDTO::getDate))
                            .collect(Collectors.toList())
            );
            return restaurantCommentDTO;
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public String addComment(Integer restaurantId, String userTokenId, String commentBody) {
        RestaurantComment restaurantComment = new RestaurantComment();

        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        Optional<User> userOptional = userRepository.findByProviderId(userTokenId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            restaurantComment.setUser(user);
            restaurantComment.setRestaurant(restaurant);
            restaurantComment.setCommentBody(commentBody);
            restaurantComment.setStatus("ACTIVE");
            restaurantComment.setCreatedAt(LocalDateTime.now());

            restaurantCommentRepository.save(restaurantComment);

            return "ok";
        } else {
            return "userTokenId";
        }
    }

    public RestaurantComment addSubComment(Restaurant restaurant, User user, String commentBody, Integer parentCommentId) {
        RestaurantComment restaurantComment = new RestaurantComment();

        restaurantComment.setUser(user);
        restaurantComment.setRestaurant(restaurant);
        restaurantComment.setCommentBody(commentBody);
        restaurantComment.setStatus("ACTIVE");
        restaurantComment.setCreatedAt(LocalDateTime.now());
        restaurantComment.setParentCommentId(parentCommentId);
        restaurantComment.setCommentLikeCount(0);

        restaurantCommentRepository.save(restaurantComment);

        return restaurantComment;
    }

    public boolean isUserLikedComment(User user, RestaurantComment restaurantComment) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        return restaurantCommentlikeOptional.isPresent();
    }

    public boolean isUserHatedComment(User user, RestaurantComment restaurantComment) {
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        return restaurantCommentdislikeOptional.isPresent();
    }

    public void likeComment(User user, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(user, restaurantComment);

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 좋아요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "unliked");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            RestaurantCommentLike restaurantCommentlike = new RestaurantCommentLike(user, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            commentLikeCountAdd(restaurantComment, 2);
            responseMap.put("status", "switched");
        } else { // 새로 좋아요를 누르는 경우
            RestaurantCommentLike restaurantCommentlike = new RestaurantCommentLike(user, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            commentLikeCountAdd(restaurantComment, 1);
            responseMap.put("status", "liked");
        }
    }

    private void commentLikeCountAdd(RestaurantComment restaurantComment, int addNum) {
        restaurantComment.setCommentLikeCount(restaurantComment.getCommentLikeCount() + addNum);
        restaurantCommentRepository.save(restaurantComment);
    }

    public void dislikeComment(User user, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(user, restaurantComment);

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 싫어요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            commentLikeCountAdd(restaurantComment, 1);
            responseMap.put("status", "unhated");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            RestaurantCommentDislike restaurantCommentdislike = new RestaurantCommentDislike(user, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -2);
            responseMap.put("status", "switched");
        } else { // 새로 싫어요를 누르는 경우
            RestaurantCommentDislike restaurantCommentdislike = new RestaurantCommentDislike(user, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "hated");
        }
    }
}
