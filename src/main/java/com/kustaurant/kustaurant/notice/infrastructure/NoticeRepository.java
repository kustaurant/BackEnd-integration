package com.kustaurant.kustaurant.notice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Integer> {
}
