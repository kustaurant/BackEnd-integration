package com.kustaurant.kustaurant.admin.modal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface HomeModalRepository extends JpaRepository<HomeModalEntity,Integer> {
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<HomeModalEntity> findById(Integer integer);
}