package com.kustaurant.kustaurant.admin.notice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Integer> {
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    List<NoticeEntity> findAll();
}
