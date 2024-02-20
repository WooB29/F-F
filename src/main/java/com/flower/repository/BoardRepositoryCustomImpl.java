package com.flower.repository;

import com.flower.dto.CommentDto;
import com.flower.dto.CommunitySearchDto;
import com.flower.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public BoardRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchByNotice(String searchBy, String searchQuery){
        if (StringUtils.equals("title",searchBy)){
            return QNotice.notice.title.like("%"+searchQuery+"%");
        }
        else if (StringUtils.equals("writer", searchBy)){
            return QNotice.notice.member.name.like("%"+searchQuery+"%");
        }
        return  null;
    }

    @Override
    public Page<Notice> getNoticePage(CommunitySearchDto communitySearchDto, Pageable pageable) {
        QueryResults<Notice> results = queryFactory.selectFrom(QNotice.notice)
                .where(searchByNotice(communitySearchDto.getSearchBy(),communitySearchDto.getSearchQuery()))
                .orderBy(QNotice.notice.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Notice> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression searchByQna(String searchBy, String searchQuery){
        if (StringUtils.equals("title",searchBy)){
            return QQna.qna.title.like("%"+searchQuery+"%");
        }
        else if (StringUtils.equals("writer", searchBy)){
            return QQna.qna.member.name.like("%"+searchQuery+"%");
        }
        return  null;
    }

    @Override
    public Page<Qna> getQnaPage(CommunitySearchDto communitySearchDto, Pageable pageable) {
        QueryResults<Qna> results = queryFactory.selectFrom(QQna.qna)
                .where(searchByQna(communitySearchDto.getSearchBy(),communitySearchDto.getSearchQuery()))
                .orderBy(QQna.qna.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Qna> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }


    public List<Comment> getNoticeCommentList(Long noticeId){
        Notice notice = queryFactory.selectFrom(QNotice.notice)
                .leftJoin(QNotice.notice.comments, QComment.comment)
                .fetchJoin()
                .where(QNotice.notice.id.eq(noticeId))
                .fetchOne();

        if (notice == null) {
            return new ArrayList<>();
        }

        for (Comment comment : notice.getComments()) {
            comment.setChildren(queryFactory.selectFrom(QComment.comment)
                    .leftJoin(QComment.comment.children)
                    .fetchJoin()
                    .where(QComment.comment.id.eq(comment.getId()))
                    .fetchOne().getChildren());
        }
        return notice.getComments();
    }

    public List<Comment> getQnaCommentList(Long qnaId){
        Qna qna = queryFactory.selectFrom(QQna.qna)
                .leftJoin(QQna.qna.comments, QComment.comment)
                .fetchJoin()
                .where(QQna.qna.id.eq(qnaId))
                .fetchOne();

        if (qna == null){
            return new ArrayList<>();
        }

        for (Comment comment : qna.getComments()){
            comment.setChildren(queryFactory.selectFrom(QComment.comment)
                    .leftJoin(QComment.comment.children)
                    .fetchJoin()
                    .where(QComment.comment.id.eq(comment.getId()))
                    .fetchOne().getChildren());
        }
        return qna.getComments();
    }



}
