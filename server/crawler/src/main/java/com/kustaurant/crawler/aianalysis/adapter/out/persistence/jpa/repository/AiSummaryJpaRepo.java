package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.rating.entity.AiSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiSummaryJpaRepo extends JpaRepository<AiSummaryEntity, Long> {

}
