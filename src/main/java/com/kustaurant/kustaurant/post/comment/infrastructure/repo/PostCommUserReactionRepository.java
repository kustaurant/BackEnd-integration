package com.kustaurant.kustaurant.post.comment.infrastructure.repo;

import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommUserReactionEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommUserReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommUserReactionRepository extends JpaRepository<PostCommUserReactionEntity, PostCommUserReactionId> {

}
