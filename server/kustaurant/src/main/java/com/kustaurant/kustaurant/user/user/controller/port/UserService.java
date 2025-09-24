package com.kustaurant.kustaurant.user.user.controller.port;

import com.kustaurant.kustaurant.user.user.domain.User;

public interface UserService {
    User getUserById(Long id);
    User create(User user);
    User update(User user);
    }
