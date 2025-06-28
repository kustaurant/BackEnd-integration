package com.kustaurant.kustaurant.user.user.service.port;

import com.kustaurant.kustaurant.post.post.domain.dto.UserDTO;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    User getById(Long userId);
    Optional<User> findByProviderId(String providerId);
    Boolean existsByNickname(Nickname nickname);
    Boolean existsByPhoneNumber(PhoneNumber phoneNumber);

    Optional<User> findById(Long userId);
    User save(User user);

    List<User> findUsersWithEvaluationCountDescending();
    List<User> findUsersByEvaluationCountForQuarter(int year, int quarter);

    int countByLoginApi(String apple);

    Map<Long, UserDTO> getUserDTOMapByIds(List<Long> ids);
}
