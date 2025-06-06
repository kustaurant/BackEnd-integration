package com.kustaurant.kustaurant.common.user.controller.port;

import com.kustaurant.kustaurant.common.post.domain.UserDTO;
import com.kustaurant.kustaurant.common.user.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getActiveUserById(Integer id);
    User create(User user);
    User update(User user);

    Map<Integer, UserDTO> getUserDTOsByIds(List<Integer> userIds);
    }
