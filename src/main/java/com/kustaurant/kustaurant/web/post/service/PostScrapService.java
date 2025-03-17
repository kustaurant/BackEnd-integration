package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.common.post.infrastructure.PostScrapRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import com.kustaurant.kustaurant.common.post.infrastructure.Post;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrap;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.post.infrastructure.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public Map<String, Object> scrapCreateOfDelete(Post post, User user){
        List<PostScrap> postScrapList =post.getPostScrapList();
        List<PostScrap> userScrapList =user.getScrapList();
        Optional<PostScrap> scrapOptional = postScrapRepository.findByUserAndPost(user,post);
        Map<String, Object> status = new HashMap<>();
        if(scrapOptional.isPresent()){
            PostScrap scrap = scrapOptional.get();
            postScrapRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status.put("scrapDelete",true);
        }
        else{
            PostScrap scrap = new PostScrap(user,post, LocalDateTime.now());
            PostScrap savedScrap= postScrapRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status.put("scrapCreated",true);

        }
        postRepository.save(post);
        userRepository.save(user);
        return status;
    }

}
