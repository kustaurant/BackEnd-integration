package com.kustaurant.mainapp.user.mypage.service;

import com.kustaurant.mainapp.user.mypage.infrastructure.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStatsService {
    private final UserStatsRepository repo;

    public void incFavoriteRestaurant(Long userId) {
        repo.addSavedRestCnt(userId, +1);
    }

    public void decFavoriteRestaurant(Long userId) {
        repo.addSavedRestCnt(userId, -1);
    }

    public void incEvaluatedRestaurant(Long userId) {
        repo.addRatedRestCnt(userId, +1);
    }

    public void decEvaluatedRestaurant(Long userId) {
        repo.addRatedRestCnt(userId, -1);
    }

    public void incPost(Long userId) {
        repo.addCommPostCnt(userId, +1);
    }

    public void decPost(Long userId) {
        repo.addCommPostCnt(userId, -1);
    }

    public void incPostComment(Long userId) {
        repo.addCommCommentCnt(userId, +1);
    }

    public void decPostComment(Long userId) {
        repo.addCommCommentCnt(userId, -1);
    }

    public void incScrappedPost(Long userId) {
        repo.addCommSavedPostCnt(userId, +1);
    }

    public void decScrappedPost(Long userId) {
        repo.addCommSavedPostCnt(userId, -1);
    }
}
