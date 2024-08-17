package com.kustaurant.restauranttier.tab1_home.repository;

import com.kustaurant.restauranttier.tab1_home.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
}
