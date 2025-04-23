package com.kustaurant.kustaurant.global.webUser;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserSecuriyService implements UserDetailsService {
    private final OUserRepository OUserRepository;

    // 인증 절차 (아이디 있는지 확인하고 없으면 권한 부여)
    @Override
    public UserDetails loadUserByUsername(String userTokenId) throws UsernameNotFoundException {
        Optional<UserEntity> siteUser = this.OUserRepository.findByProviderId(userTokenId);
        if(siteUser.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        UserEntity UserEntity = siteUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if("admin".equals(userTokenId)){
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        }else{
            authorities.add(new SimpleGrantedAuthority((UserRole.USER.getValue())));
        }
        // User 엔티티 아님 , 시큐리티에서 제공하는 User 클래스
        return new org.springframework.security.core.userdetails.User(UserEntity.getProviderId(), "",authorities);
    }
}
