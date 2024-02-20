package com.flower.repository;

import com.flower.entity.DeleteItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteItemImgRepository extends JpaRepository<DeleteItemImg, Long> {

    DeleteItemImg findByDeleteItemIdAndRepImgYn(Long deleteItemId, String repImgYn);
}
