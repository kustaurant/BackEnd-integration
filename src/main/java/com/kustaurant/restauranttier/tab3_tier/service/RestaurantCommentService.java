package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantCommentdislike;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantCommentlike;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantCommentDislikeRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantCommentLikeRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantCommentRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantRepository;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import com.kustaurant.restauranttier.common.exception.exception.DataNotFoundException;
import com.kustaurant.restauranttier.tab3_tier.etc.EnumSortComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestaurantCommentService {

    private final RestaurantCommentRepository restaurantCommentRepository;
    private final RestaurantCommentLikeRepository restaurantCommentLikeRepository;
    private final RestaurantCommentDislikeRepository restaurantCommentDislikeRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public String addComment(Integer restaurantId, String userTokenId, String commentBody) {
        RestaurantComment restaurantComment = new RestaurantComment();

        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        Optional<User> userOptional = userRepository.findByNaverProviderId(userTokenId);
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

    public RestaurantComment getComment(int commentId) {
        Optional<RestaurantComment> restaurantCommentOptional = restaurantCommentRepository.findByCommentId(commentId);
        if (restaurantCommentOptional.isPresent()) {
            return restaurantCommentOptional.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public Integer getCommentLikeScore(int commentId) {
        return restaurantCommentRepository.findLikeDislikeDiffByCommentId(commentId);
    }

    public List<Object[]> getCommentList(int restaurantId, EnumSortComment sortComment) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        if (sortComment == EnumSortComment.POPULAR) {
            return restaurantCommentRepository.findOrderPopular(restaurant);
        } else if (sortComment == EnumSortComment.LATEST) {
            return restaurantCommentRepository.findOrderLatest(restaurant);
        } else {
            return null;
        }
    }

    public List<Object[]> getCommentList(int restaurantId, EnumSortComment sortComment, User user) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        if (sortComment == EnumSortComment.POPULAR) {
            return restaurantCommentRepository.findOrderPopular(restaurant, user);
        } else if (sortComment == EnumSortComment.LATEST) {
            return restaurantCommentRepository.findOrderLatest(restaurant, user);
        } else {
            return null;
        }
    }

    /*public void likeComment(User user, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentlike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        Optional<RestaurantCommentdislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(user, restaurantComment);

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException("Both like and dislike exist for the same comment.");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            user.getRestaurantCommentlikeList().remove(restaurantCommentlikeOptional.get());
            responseMap.put("status", "unliked");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            user.getRestaurantCommentdislikeList().remove(restaurantCommentdislikeOptional.get());
            RestaurantCommentlike restaurantCommentlike = new RestaurantCommentlike(user, restaurantComment);
            user.getRestaurantCommentlikeList().add(restaurantCommentlike);
            responseMap.put("status", "switched");
        } else { // 새로 댓글을 다는 경우
            RestaurantCommentlike restaurantCommentlike = new RestaurantCommentlike(user, restaurantComment);
            user.getRestaurantCommentlikeList().add(restaurantCommentlike);
            responseMap.put("status", "liked");
        }
        userRepository.save(user);
    }*/

    public boolean isUserLikedComment(User user, RestaurantComment restaurantComment) {
        Optional<RestaurantCommentlike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        return restaurantCommentlikeOptional.isPresent();
    }

    public boolean isUserHatedComment(User user, RestaurantComment restaurantComment) {
        Optional<RestaurantCommentdislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        return restaurantCommentdislikeOptional.isPresent();
    }

    public void likeComment(User user, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentlike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        Optional<RestaurantCommentdislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(user, restaurantComment);

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException("Both like and dislike exist for the same comment.");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            responseMap.put("status", "unliked");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            RestaurantCommentlike restaurantCommentlike = new RestaurantCommentlike(user, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            responseMap.put("status", "switched");
        } else { // 새로 좋아요를 누르는 경우
            RestaurantCommentlike restaurantCommentlike = new RestaurantCommentlike(user, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            responseMap.put("status", "liked");
        }
    }

    public void dislikeComment(User user, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentlike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(user, restaurantComment);
        Optional<RestaurantCommentdislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(user, restaurantComment);

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException("Both like and dislike exist for the same comment.");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            responseMap.put("status", "unhated");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            RestaurantCommentdislike restaurantCommentdislike = new RestaurantCommentdislike(user, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            responseMap.put("status", "switched");
        } else { // 새로 싫어요를 누르는 경우
            RestaurantCommentdislike restaurantCommentdislike = new RestaurantCommentdislike(user, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            responseMap.put("status", "hated");
        }
    }

    public boolean deleteComment(Integer commentId, User user) {
        Optional<RestaurantComment> restaurantCommentOptional = restaurantCommentRepository.findByCommentId(commentId);

        if (restaurantCommentOptional.isPresent()) {
            RestaurantComment restaurantComment = restaurantCommentOptional.get();
            restaurantComment.setStatus("DELETED");
            restaurantCommentRepository.save(restaurantComment);
            return true;
        } else {
            return false;
        }
    }
}
