package com.flower.repository;

import com.flower.entity.DeleteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteItemRepository extends JpaRepository<DeleteItem, Long> {
}
