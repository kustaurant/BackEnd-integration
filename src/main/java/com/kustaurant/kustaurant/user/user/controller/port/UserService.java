package com.kustaurant.kustaurant.user.user.controller.port;

import com.kustaurant.kustaurant.common.dto.UserSummary;
import com.kustaurant.kustaurant.user.user.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getUserById(Long id);
    User create(User user);
    User update(User user);
    }
