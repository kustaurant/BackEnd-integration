package com.kustaurant.restauranttier.tab1_home.repository;

import com.kustaurant.restauranttier.tab1_home.entity.HomeModal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeModalRepository extends JpaRepository<HomeModal,Integer> {
    HomeModal getHomeModalByModalId(Integer Id);
}
