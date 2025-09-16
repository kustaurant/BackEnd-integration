package com.kustaurant.kustaurant.v1.community;

import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.user.rank.controller.response.UserRankResponse;
import com.kustaurant.kustaurant.v1.community.dto.PostDTO;
import com.kustaurant.kustaurant.v1.community.dto.UserDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface CommunityCompatMapper {
    // 게시글 목록 조회
    @Mappings({
            @Mapping(target = "postId",        source = "postId",       qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "postCategory",  source = "category",     qualifiedByName = "postCategoryToString"),
            @Mapping(target = "postTitle",     source = "title"),
            @Mapping(target = "postImgUrl",    source = "photoUrl"),
            @Mapping(target = "postBody",      source = "body"),
            @Mapping(target = "likeCount",     source = "totalLikes",   qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "commentCount",  source = "commentCount", qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "timeAgo",       source = "timeAgo")
    })
    PostDTO toLegacy(PostListResponse src);
    List<PostDTO> toLegacyPostList(List<PostListResponse> src);

    // 유저 랭킹
    @Mappings({
            @Mapping(target = "userNickname", source = "nickname"),
            @Mapping(target = "rankImg",      source = "iconUrl"),
            @Mapping(target = "evaluationCount", source = "evaluationCount"),
            // 주의: UserDTO 필드명이 'Integer Rank;' 이지만 Java Bean 프로퍼티명은 'rank' 입니다.
            // Lombok @Data가 getRank()/setRank()를 생성하므로 target은 "rank"로 지정해야 합니다.
            @Mapping(target = "rank",         source = "rank")
            // userId 는 v1 DTO에 없으므로 매핑하지 않음(무시)
    })
    UserDTO toLegacy(UserRankResponse src);
    List<UserDTO> toLegacyUserRankList(List<UserRankResponse> src);


}
