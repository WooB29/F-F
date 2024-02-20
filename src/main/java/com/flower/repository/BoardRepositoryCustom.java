package com.flower.repository;

import com.flower.dto.CommunitySearchDto;
import com.flower.entity.Comment;
import com.flower.entity.Notice;
import com.flower.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepositoryCustom {

    Page<Notice> getNoticePage(CommunitySearchDto communitySearchDto, Pageable pageable);

    Page<Qna> getQnaPage(CommunitySearchDto communitySearchDto, Pageable pageable);

    List<Comment> getNoticeCommentList(Long noticeId);

    List<Comment> getQnaCommentList(Long qnaId);
}
