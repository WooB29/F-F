package com.flower.repository;

import com.flower.entity.Notice;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NoticeRepository extends JpaRepository<Notice, Long> , QuerydslPredicateExecutor<Notice>, BoardRepositoryCustom {

    @Modifying
    @Query("update Notice n set n.hits = n.hits + 1 where n.id = :id")
    int updateHits(Long id);
}
