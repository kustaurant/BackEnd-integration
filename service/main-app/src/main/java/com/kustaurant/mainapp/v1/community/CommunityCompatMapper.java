package com.kustaurant.mainapp.v1.community;

import com.kustaurant.mainapp.post.community.controller.response.PostListResponse;
import com.kustaurant.mainapp.user.rank.controller.response.UserRankResponse;
import com.kustaurant.mainapp.v1.common.MapStructConverters;
import com.kustaurant.mainapp.v1.community.dto.PostDTO;
import com.kustaurant.mainapp.v1.community.dto.UserDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = { MapStructConverters.class }
)
public interface CommunityCompatMapper {
    // 게시글 목록 조회
    @BeanMapping(ignoreByDefault = true) // <- 선언 안 된 타겟 프로퍼티 전부 무시
    @Mappings({
            @Mapping(target = "postId",        source = "postId",       qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "postCategory",  source = "category",     qualifiedByName = "postCategoryToString"),
            @Mapping(target = "postTitle",     source = "title"),
            @Mapping(target = "postPhotoImgUrl", source = "photoUrl"),
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
            @Mapping(target = "rank",         source = "rank")
    })
    UserDTO toLegacy(UserRankResponse src);
    List<UserDTO> toLegacyUserRankList(List<UserRankResponse> src);


}
