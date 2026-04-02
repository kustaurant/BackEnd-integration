package com.kustaurant.kustaurant.v1.mypage;

import com.kustaurant.kustaurant.admin.notice.Notice;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostCommentResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostsResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRatedRestaurantResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse;
import com.kustaurant.kustaurant.v1.common.MapStructConverters;
import com.kustaurant.kustaurant.v1.mypage.dto.EvaluatedRestaurantInfoDTO;
import com.kustaurant.kustaurant.v1.mypage.dto.FavoriteRestaurantInfoDTO;
import com.kustaurant.kustaurant.v1.mypage.dto.MypagePostCommentDTO;
import com.kustaurant.kustaurant.v1.mypage.dto.MypagePostDTO;
import com.kustaurant.kustaurant.v1.mypage.dto.NoticeDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-25T20:06:04+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class MypageCompatMapperImpl implements MypageCompatMapper {

    @Override
    public EvaluatedRestaurantInfoDTO toLegacy(MyRatedRestaurantResponse src) {

        Integer restaurantId = null;
        String restaurantComment = null;
        String restaurantName = null;
        String restaurantImgURL = null;
        String cuisine = null;
        Double evaluationScore = null;
        List<String> evaluationItemScores = null;
        if ( src != null ) {
            if ( src.restaurantId() != null ) {
                restaurantId = MapStructConverters.longToIntegerExact( src.restaurantId() );
            }
            if ( src.evaluationBody() != null ) {
                restaurantComment = src.evaluationBody();
            }
            if ( src.restaurantName() != null ) {
                restaurantName = src.restaurantName();
            }
            if ( src.restaurantImgURL() != null ) {
                restaurantImgURL = src.restaurantImgURL();
            }
            if ( src.cuisine() != null ) {
                cuisine = src.cuisine();
            }
            if ( src.evaluationScore() != null ) {
                evaluationScore = src.evaluationScore();
            }
            List<String> list = src.evaluationItemScores();
            if ( list != null ) {
                evaluationItemScores = new ArrayList<String>( list );
            }
        }

        EvaluatedRestaurantInfoDTO evaluatedRestaurantInfoDTO = new EvaluatedRestaurantInfoDTO( restaurantName, restaurantId, restaurantImgURL, cuisine, evaluationScore, restaurantComment, evaluationItemScores );

        if ( src != null ) {
        }

        return evaluatedRestaurantInfoDTO;
    }

    @Override
    public List<EvaluatedRestaurantInfoDTO> toLegacyRatedList(List<MyRatedRestaurantResponse> src) {
        if ( src == null ) {
            return new ArrayList<EvaluatedRestaurantInfoDTO>();
        }

        List<EvaluatedRestaurantInfoDTO> list = new ArrayList<EvaluatedRestaurantInfoDTO>( src.size() );
        for ( MyRatedRestaurantResponse myRatedRestaurantResponse : src ) {
            list.add( toLegacy( myRatedRestaurantResponse ) );
        }

        return list;
    }

    @Override
    public MypagePostDTO toLegacy(MyPostsResponse src) {

        MypagePostDTO mypagePostDTO = new MypagePostDTO();

        if ( src != null ) {
            if ( src.postId() != null ) {
                mypagePostDTO.setPostId( MapStructConverters.longToIntegerExact( src.postId() ) );
            }
            if ( src.postCategory() != null ) {
                mypagePostDTO.setPostCategory( MapStructConverters.postCategoryToString( src.postCategory() ) );
            }
            if ( src.postTitle() != null ) {
                mypagePostDTO.setPostTitle( src.postTitle() );
            }
            if ( src.postImgUrl() != null ) {
                mypagePostDTO.setPostImgUrl( src.postImgUrl() );
            }
            if ( src.likeCount() != null ) {
                mypagePostDTO.setLikeCount( MapStructConverters.longToIntegerExact( src.likeCount() ) );
            }
            if ( src.commentCount() != null ) {
                mypagePostDTO.setCommentCount( MapStructConverters.longToIntegerExact( src.commentCount() ) );
            }
        }
        mypagePostDTO.setPostBody( src.postBodyPreview() );
        mypagePostDTO.setTimeAgo( src.timeAgo() );

        return mypagePostDTO;
    }

    @Override
    public List<MypagePostDTO> toLegacyPostList(List<MyPostsResponse> src) {
        if ( src == null ) {
            return new ArrayList<MypagePostDTO>();
        }

        List<MypagePostDTO> list = new ArrayList<MypagePostDTO>( src.size() );
        for ( MyPostsResponse myPostsResponse : src ) {
            list.add( toLegacy( myPostsResponse ) );
        }

        return list;
    }

    @Override
    public FavoriteRestaurantInfoDTO toLegacy(MyRestaurantResponse src) {

        FavoriteRestaurantInfoDTO favoriteRestaurantInfoDTO = new FavoriteRestaurantInfoDTO();

        if ( src != null ) {
            if ( src.restaurantId() != null ) {
                favoriteRestaurantInfoDTO.setRestaurantId( MapStructConverters.longToIntegerExact( src.restaurantId() ) );
            }
            if ( src.restaurantName() != null ) {
                favoriteRestaurantInfoDTO.setRestaurantName( src.restaurantName() );
            }
            if ( src.restaurantImgURL() != null ) {
                favoriteRestaurantInfoDTO.setRestaurantImgURL( src.restaurantImgURL() );
            }
            if ( src.mainTier() != null ) {
                favoriteRestaurantInfoDTO.setMainTier( src.mainTier() );
            }
            if ( src.restaurantType() != null ) {
                favoriteRestaurantInfoDTO.setRestaurantType( src.restaurantType() );
            }
            if ( src.restaurantPosition() != null ) {
                favoriteRestaurantInfoDTO.setRestaurantPosition( src.restaurantPosition() );
            }
        }

        return favoriteRestaurantInfoDTO;
    }

    @Override
    public List<FavoriteRestaurantInfoDTO> toLegacyFavList(List<MyRestaurantResponse> src) {
        if ( src == null ) {
            return new ArrayList<FavoriteRestaurantInfoDTO>();
        }

        List<FavoriteRestaurantInfoDTO> list = new ArrayList<FavoriteRestaurantInfoDTO>( src.size() );
        for ( MyRestaurantResponse myRestaurantResponse : src ) {
            list.add( toLegacy( myRestaurantResponse ) );
        }

        return list;
    }

    @Override
    public MypagePostCommentDTO toLegacy(MyPostCommentResponse src) {

        MypagePostCommentDTO mypagePostCommentDTO = new MypagePostCommentDTO();

        if ( src != null ) {
            if ( src.postId() != null ) {
                mypagePostCommentDTO.setPostId( MapStructConverters.longToIntegerExact( src.postId() ) );
            }
            if ( src.postCategory() != null ) {
                mypagePostCommentDTO.setPostCategory( MapStructConverters.postCategoryToString( src.postCategory() ) );
            }
            if ( src.postTitle() != null ) {
                mypagePostCommentDTO.setPostTitle( src.postTitle() );
            }
            if ( src.body() != null ) {
                mypagePostCommentDTO.setPostcommentBody( src.body() );
            }
            if ( src.likeCount() != null ) {
                mypagePostCommentDTO.setCommentlikeCount( MapStructConverters.longToIntegerExact( src.likeCount() ) );
            }
        }

        return mypagePostCommentDTO;
    }

    @Override
    public List<MypagePostCommentDTO> toLegacyCommentList(List<MyPostCommentResponse> src) {
        if ( src == null ) {
            return new ArrayList<MypagePostCommentDTO>();
        }

        List<MypagePostCommentDTO> list = new ArrayList<MypagePostCommentDTO>( src.size() );
        for ( MyPostCommentResponse myPostCommentResponse : src ) {
            list.add( toLegacy( myPostCommentResponse ) );
        }

        return list;
    }

    @Override
    public NoticeDTO toLegacy(Notice src) {

        NoticeDTO noticeDTO = new NoticeDTO();

        if ( src != null ) {
            if ( src.getTitle() != null ) {
                noticeDTO.setNoticeTitle( src.getTitle() );
            }
            if ( src.getHref() != null ) {
                noticeDTO.setNoticeLink( src.getHref() );
            }
            if ( src.getCreatedAt() != null ) {
                noticeDTO.setCreatedDate( src.getCreatedAt() );
            }
        }

        return noticeDTO;
    }

    @Override
    public List<NoticeDTO> toLegacyNoticeList(List<Notice> src) {
        if ( src == null ) {
            return new ArrayList<NoticeDTO>();
        }

        List<NoticeDTO> list = new ArrayList<NoticeDTO>( src.size() );
        for ( Notice notice : src ) {
            list.add( toLegacy( notice ) );
        }

        return list;
    }
}
