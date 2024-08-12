//package com.site.restauranttier;
//
//import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantRepository;
//import com.kustaurant.restauranttier.tab3_tier.repository.SituationRepository;
//import com.kustaurant.restauranttier.tab4_community.entity.Post;
//import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
//import com.kustaurant.restauranttier.tab4_community.entity.PostPhoto;
//import com.kustaurant.restauranttier.tab4_community.repository.PostCommentRepository;
//import com.kustaurant.restauranttier.tab4_community.repository.PostPhotoRepository;
//import com.kustaurant.restauranttier.tab4_community.repository.PostRepository;
//import com.kustaurant.restauranttier.tab5_mypage.entity.User;
//import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@SpringBootTest
//class RestaurantTierApplicationTests {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private RestaurantRepository restaurantRepository;
//    @Autowired
//    private SituationRepository situationRepository;
//    @Autowired
//    private PostRepository postRepository;
//    @Autowired
//    private PostPhotoRepository postPhotoRepository;
//    @Autowired
//    private PostCommentRepository postCommentRepository;
//    private static final Logger logger = LoggerFactory.getLogger(RestaurantTierApplicationTests.class);
//
//    // 테스트 데이터 추가
//    @Test
//    @Transactional
//    @Rollback(false)
//    void textCommunity() {
//
//        // 커뮤니티 테스트
//
//        Post postsaved = new Post("부탄츄 후기", "부탄츄 건대점에 다녀왔습니다", "자유게시판", "ACTIVE", LocalDateTime.now());
//        Optional<User> userOptional = userRepository.findById(1);
//        User user = userOptional.get();
//        postsaved.setUser(user);
//        PostPhoto postPhoto = new PostPhoto("https://mblogthumb-phinf.pstatic.net/MjAyMDAxMTlfMTEg/MDAxNTc5NDMyNjY5OTg4.XkEOiHNJDaEgWAawh-IzZFPkovVqLXlQRcdDiWFOW5gg.h5WRn-eHCJ9FxK2ou6P892Zt0Xd1_MrBWOWc6t7VMJAg.JPEG.effy0424/1579432668513.jpg?type=w800", "ACTIVE");
//        postsaved.getPostPhotoList().add(postPhoto);
//        postRepository.save(postsaved);
//
//
//        Post post = postRepository.findByPostTitle(postsaved.getPostTitle());
//        PostComment comment = new PostComment("좋은 글입니다","ACTIVE",LocalDateTime.now(),post,user);
//        PostComment savedcomment = postCommentRepository.save(comment);
//        // photo와 user에서 comment 매핑
//        post.getPostCommentList().add(savedcomment);
//        user.getPostCommentList().add(savedcomment);
//        //        포토와 user에서 post를 매핑
//        postPhoto.setPost(post);
//        user.getPostList().add(post);
//
//        postPhotoRepository.save(postPhoto);
//        userRepository.save(user);
//
//
//
//
//
//    }
//
//}
