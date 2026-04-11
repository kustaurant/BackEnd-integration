package com.kustaurant.kustaurant.admin.IGCrawl.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IgCrawlRawRepository extends JpaRepository<IgCrawlRawEntity, Long> {
    void deleteBySourceAccount(String sourceAccount);
    List<IgCrawlRawEntity> findBySourceAccount(String sourceAccount);
}
