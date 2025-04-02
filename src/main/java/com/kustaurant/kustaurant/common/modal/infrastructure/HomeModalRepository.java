package com.kustaurant.kustaurant.common.modal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeModalRepository extends JpaRepository<HomeModalEntity,Integer> {
    HomeModalEntity getHomeModalByModalId(Integer Id);
}
