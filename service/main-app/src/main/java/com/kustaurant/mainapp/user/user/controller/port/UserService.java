package com.kustaurant.mainapp.user.user.controller.port;

import com.kustaurant.mainapp.common.dto.UserSummary;
import com.kustaurant.mainapp.user.user.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getUserById(Long id);
    User create(User user);
    User update(User user);
    }
