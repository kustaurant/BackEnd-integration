package com.kustaurant.restauranttier.tab4_community.service;


import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
import com.kustaurant.restauranttier.tab4_community.repository.PostApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostScrapApiRepository;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapApiService {
    private final PostScrapApiRepository postScrapApiRepository;
    private final PostApiRepository postApiRepository;
    private final UserRepository userRepository;
    public Map<String, Object> scrapCreateOrDelete(Post post, User user){
        List<PostScrap> postScrapList =post.getPostScrapList();
        List<PostScrap> userScrapList =user.getScrapList();
        Optional<PostScrap> scrapOptional = postScrapApiRepository.findByUserAndPost(user,post);
        Map<String, Object> status = new HashMap<>();
        if(scrapOptional.isPresent()){
            PostScrap scrap = scrapOptional.get();
            postScrapApiRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status.put("scrapDelete",true);
        }
        else{
            PostScrap scrap = new PostScrap(user,post, LocalDateTime.now());
            PostScrap savedScrap= postScrapApiRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status.put("scrapCreated",true);

        }
        postApiRepository.save(post);
        userRepository.save(user);
        return status;
    }

}
