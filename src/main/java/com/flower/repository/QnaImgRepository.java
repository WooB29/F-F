package com.flower.repository;

import com.flower.entity.NoticeImg;
import com.flower.entity.QnaImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaImgRepository extends JpaRepository<QnaImg, Long> {

    List<QnaImg> findByQnaIdOrderByIdAsc(Long qnaId);

    List<QnaImg> findByQnaId(Long qnaId);
}
