package com.kustaurant.kustaurant.admin.adminPage.infrastructure;

import com.kustaurant.kustaurant.admin.adminPage.dto.PagedUserResponse;
import com.kustaurant.kustaurant.admin.adminPage.dto.UserListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity.userEntity;

@Repository
@RequiredArgsConstructor
public class AdminUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public PagedUserResponse getNewUsers(Pageable pageable) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<UserListResponse> users = queryFactory
                .select(Projections.constructor(UserListResponse.class,
                        userEntity.id,
                        userEntity.nickname.value,
                        userEntity.loginApi,
                        userEntity.status.stringValue(),
                        userEntity.createdAt,
                        userEntity.createdAt.as("lastLoginAt") // 신규 유저는 마지막 로그인 시간을 가입일로 설정
                ))
                .from(userEntity)
                .where(userEntity.createdAt.after(sevenDaysAgo))
                .orderBy(userEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalElements = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .where(userEntity.createdAt.after(sevenDaysAgo))
                .fetchOne();

        totalElements = totalElements != null ? totalElements : 0L;
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return new PagedUserResponse(
                users,
                totalElements,
                totalPages,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getPageNumber() < totalPages - 1,
                pageable.getPageNumber() > 0
        );
    }

    public PagedUserResponse getAllUsers(Pageable pageable) {
        List<UserListResponse> users = queryFactory
                .select(Projections.constructor(UserListResponse.class,
                        userEntity.id,
                        userEntity.nickname.value,
                        userEntity.loginApi,
                        userEntity.status.stringValue(),
                        userEntity.createdAt,
                        userEntity.updatedAt.coalesce(userEntity.createdAt).as("lastLoginAt") // 업데이트 시간이 없으면 가입일
                ))
                .from(userEntity)
                .orderBy(userEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalElements = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .fetchOne();

        totalElements = totalElements != null ? totalElements : 0L;
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return new PagedUserResponse(
                users,
                totalElements,
                totalPages,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getPageNumber() < totalPages - 1,
                pageable.getPageNumber() > 0
        );
    }

    public Long getNewUsersCount() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        
        Long count = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .where(userEntity.createdAt.after(sevenDaysAgo))
                .fetchOne();
                
        return count != null ? count : 0L;
    }

    public Long getTotalNaverUsers() {
        Long count = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .where(userEntity.loginApi.eq("naver"))
                .fetchOne();
        
        return count != null ? count : 0L;
    }

    public Long getTotalAppleUsers() {
        Long count = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .where(userEntity.loginApi.eq("apple"))
                .fetchOne();
        
        return count != null ? count : 0L;
    }

    public Long getTotalUsers() {
        Long count = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .fetchOne();
        
        return count != null ? count : 0L;
    }
}