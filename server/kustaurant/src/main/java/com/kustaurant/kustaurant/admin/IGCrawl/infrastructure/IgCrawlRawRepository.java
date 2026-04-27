package com.kustaurant.kustaurant.admin.IGCrawl.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IgCrawlRawRepository extends JpaRepository<IgCrawlRawEntity, Long> {
    void deleteBySourceAccount(String sourceAccount);
    List<IgCrawlRawEntity> findBySourceAccount(String sourceAccount);

    @Query("""
        select r.shortCode
        from IgCrawlRawEntity r
        where r.sourceAccount = :sourceAccount
          and r.shortCode in :shortCodes
    """)
    List<String> findExistingShortCodes(
            @Param("sourceAccount") String sourceAccount,
            @Param("shortCodes") Collection<String> shortCodes
    );
}
