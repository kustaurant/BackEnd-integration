package com.kustaurant.kustaurant.common.user.controller.port;

import com.kustaurant.kustaurant.common.user.domain.User;

public interface UserService {
    User getActiveUserById(Integer id);
    User create(User user);
    User update(User user);
}
