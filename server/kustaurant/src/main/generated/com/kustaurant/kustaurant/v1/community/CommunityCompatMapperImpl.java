package com.kustaurant.kustaurant.v1.community;

import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.user.rank.controller.response.UserRankResponse;
import com.kustaurant.kustaurant.v1.common.MapStructConverters;
import com.kustaurant.kustaurant.v1.community.dto.PostDTO;
import com.kustaurant.kustaurant.v1.community.dto.UserDTO;
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
public class CommunityCompatMapperImpl implements CommunityCompatMapper {

    @Override
    public PostDTO toLegacy(PostListResponse src) {

        PostDTO postDTO = new PostDTO();

        if ( src != null ) {
            if ( src.postId() != null ) {
                postDTO.setPostId( MapStructConverters.longToIntegerExact( src.postId() ) );
            }
            if ( src.category() != null ) {
                postDTO.setPostCategory( MapStructConverters.postCategoryToString( src.category() ) );
            }
            if ( src.title() != null ) {
                postDTO.setPostTitle( src.title() );
            }
            if ( src.photoUrl() != null ) {
                postDTO.setPostPhotoImgUrl( src.photoUrl() );
            }
            if ( src.body() != null ) {
                postDTO.setPostBody( src.body() );
            }
            postDTO.setLikeCount( MapStructConverters.longToIntegerExact( src.totalLikes() ) );
            postDTO.setCommentCount( MapStructConverters.longToIntegerExact( src.commentCount() ) );
            if ( src.timeAgo() != null ) {
                postDTO.setTimeAgo( src.timeAgo() );
            }
        }

        return postDTO;
    }

    @Override
    public List<PostDTO> toLegacyPostList(List<PostListResponse> src) {
        if ( src == null ) {
            return new ArrayList<PostDTO>();
        }

        List<PostDTO> list = new ArrayList<PostDTO>( src.size() );
        for ( PostListResponse postListResponse : src ) {
            list.add( toLegacy( postListResponse ) );
        }

        return list;
    }

    @Override
    public UserDTO toLegacy(UserRankResponse src) {

        UserDTO userDTO = new UserDTO();

        if ( src != null ) {
            if ( src.nickname() != null ) {
                userDTO.setUserNickname( src.nickname() );
            }
            if ( src.iconUrl() != null ) {
                userDTO.setRankImg( src.iconUrl() );
            }
            userDTO.setEvaluationCount( src.evaluationCount() );
            userDTO.setRank( src.rank() );
        }

        return userDTO;
    }

    @Override
    public List<UserDTO> toLegacyUserRankList(List<UserRankResponse> src) {
        if ( src == null ) {
            return new ArrayList<UserDTO>();
        }

        List<UserDTO> list = new ArrayList<UserDTO>( src.size() );
        for ( UserRankResponse userRankResponse : src ) {
            list.add( toLegacy( userRankResponse ) );
        }

        return list;
    }
}
