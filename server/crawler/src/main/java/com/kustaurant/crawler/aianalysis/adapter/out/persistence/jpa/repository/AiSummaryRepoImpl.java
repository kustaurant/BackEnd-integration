package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiSummaryRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.support.AiSummaryMapper;
import com.kustaurant.crawler.aianalysis.domain.model.AiSummary;
import com.kustaurant.jpa.rating.entity.AiSummaryEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AiSummaryRepoImpl implements AiSummaryRepo {

    private final AiSummaryJpaRepo jpaRepo;

    @Override
    public void save(AiSummary aiSummary) {
        jpaRepo.save(AiSummaryMapper.from(aiSummary));
    }

    @Override
    public Optional<AiSummary> findByRestaurantId(long id) {
        Optional<AiSummaryEntity> entity = jpaRepo.findById(id);

        return entity.map(AiSummaryMapper::from);
    }
}
