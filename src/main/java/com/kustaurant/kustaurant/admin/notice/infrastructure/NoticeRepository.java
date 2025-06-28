package com.kustaurant.kustaurant.admin.notice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Integer> {
}
