package com.kustaurant.kustaurant.v1.mypage;

import com.kustaurant.kustaurant.admin.notice.Notice;
import com.kustaurant.kustaurant.v1.mypage.dto.EvaluatedRestaurantInfoDTO;
import com.kustaurant.kustaurant.v1.mypage.dto.FavoriteRestaurantInfoDTO;
import com.kustaurant.kustaurant.v1.mypage.dto.MypagePostCommentDTO;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostCommentResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostsResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRatedRestaurantResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse;
import com.kustaurant.kustaurant.v1.common.MapStructConverters;
import com.kustaurant.kustaurant.v1.mypage.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,               // 빠진 매핑은 컴파일 에러로
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,     // NPE 방지
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = { MapStructConverters.class }
)
public interface MypageCompatMapper {

    // 1) 평가한 식당 목록: MyRatedRestaurantResponse -> EvaluatedRestaurantInfoDTO
    @Mappings({
            @Mapping(target = "restaurantId", source = "restaurantId", qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "restaurantComment", source = "evaluationBody")
    })
    EvaluatedRestaurantInfoDTO toLegacy(MyRatedRestaurantResponse src);
    List<EvaluatedRestaurantInfoDTO> toLegacyRatedList(List<MyRatedRestaurantResponse> src);


    // 2) 내가 쓴 커뮤니티 작성 글: MyPostsResponse -> MypagePostDTO
    @Mappings({
            @Mapping(target = "postId",        source = "postId",        qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "postCategory",  source = "postCategory",   qualifiedByName = "postCategoryToString"),
            @Mapping(target = "postTitle",     source = "postTitle"),
            @Mapping(target = "postImgUrl",    source = "postImgUrl"),
            @Mapping(target = "postBody",      expression = "java(src.postBodyPreview())"),
            @Mapping(target = "likeCount",     source = "likeCount",      qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "commentCount",  source = "commentCount",   qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "timeAgo",       expression = "java(src.timeAgo())")
    })
    MypagePostDTO toLegacy(MyPostsResponse src);
    List<MypagePostDTO> toLegacyPostList(List<MyPostsResponse> src);


    // 3) 즐겨찾기 식당: MyRestaurantResponse -> FavoriteRestaurantInfoDTO
    @Mapping(target = "restaurantId", source = "restaurantId", qualifiedByName = "longToIntegerExact")
    FavoriteRestaurantInfoDTO toLegacy(MyRestaurantResponse src);
    List<FavoriteRestaurantInfoDTO> toLegacyFavList(List<MyRestaurantResponse> src);


    // 4) 댓글 목록: MyPostCommentResponse -> MypagePostCommentDTO
    @Mappings({
            @Mapping(target = "postId",            source = "postId",     qualifiedByName = "longToIntegerExact"),
            @Mapping(target = "postCategory",      source = "postCategory",qualifiedByName = "postCategoryToString"),
            @Mapping(target = "postTitle",         source = "postTitle"),
            @Mapping(target = "postcommentBody",   source = "body"),
            @Mapping(target = "commentlikeCount",  source = "likeCount",  qualifiedByName = "longToIntegerExact")
    })
    MypagePostCommentDTO toLegacy(MyPostCommentResponse src);

    List<MypagePostCommentDTO> toLegacyCommentList(List<MyPostCommentResponse> src);


    // 5) 공지: Notice -> NoticeDTO
    @Mappings({
            @Mapping(target = "noticeTitle", source = "title"),
            @Mapping(target = "noticeLink",  source = "href"),
            @Mapping(target = "createdDate", source = "createdAt")
    })
    NoticeDTO toLegacy(Notice src);
    List<NoticeDTO> toLegacyNoticeList(List<Notice> src);
}
