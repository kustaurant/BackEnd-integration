package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.mock.FakePostRepository;
import com.kustaurant.kustaurant.mock.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

class PostServiceTest {

    private PostService postService;

    @BeforeEach
    void init(){
        FakePostRepository fakePostRepository = new FakePostRepository();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
    }
    @Test
    void 유저는_존재하는_게시글을_조회할_수_있다(){

    }

}