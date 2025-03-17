package com.kustaurant.kustaurant.common.modal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeModalRepository extends JpaRepository<HomeModal,Integer> {
    HomeModal getHomeModalByModalId(Integer Id);
}
