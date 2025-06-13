package com.kustaurant.kustaurant.user.controller.port;

import com.kustaurant.kustaurant.post.domain.UserDTO;
import com.kustaurant.kustaurant.user.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getActiveUserById(Integer id);
    User create(User user);
    User update(User user);

    Map<Integer, UserDTO> getUserDTOsByIds(List<Integer> userIds);
    }
