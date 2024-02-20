package com.flower.repository;

import com.flower.entity.NoticeImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeImgRepository extends JpaRepository<NoticeImg, Long>{
    List<NoticeImg> findByNoticeId(Long noticeId);

    List<NoticeImg> findByNoticeIdOrderByIdAsc(Long noticeId);
}
