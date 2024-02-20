package com.flower.repository;

import com.flower.constant.Role;
import com.flower.dto.MemberSearchDto;
import com.flower.entity.Member;
import com.flower.entity.QMember;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{
    private JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchWayEq(String searchWay){
        return StringUtils.isEmpty(searchWay) ? null : QMember.member.way.like("%"+searchWay+"%");
    }

    private BooleanExpression searchRoleEq(Role searchRole){
        return searchRole == null ?
                null : QMember.member.role.eq(searchRole);

    }

    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all",searchDateType) || searchDateType == null){
            return null;
        }
        else if (StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        }
        else if (StringUtils.equals("1w",searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }
        else if (StringUtils.equals("1m",searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }
        else if (StringUtils.equals("6m",searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }
        return QMember.member.regTime.after(dateTime);
    }


    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if (StringUtils.equals("id",searchBy)){
            return QMember.member.id.like("%"+searchQuery+"%");
        }
        if (StringUtils.equals("name",searchBy)){
            return QMember.member.name.like("%"+searchQuery+"%");
        }
        if (StringUtils.equals("phone", searchBy)){
            return QMember.member.phone.like("%"+searchQuery+"%");
        }
        return  null;
    }
    @Override
    public Page<Member> getAdminMemberPage (MemberSearchDto memberSearchDto, Pageable pageable) {
        QueryResults<Member> results = queryFactory.selectFrom(QMember.member)
                .where(regDtsAfter(memberSearchDto.getSearchDateType()),
                        searchRoleEq(memberSearchDto.getSearchRole()),
                        searchWayEq(memberSearchDto.getSearchWay()),
                        searchByLike(memberSearchDto.getSearchName(),memberSearchDto.getSearchQuery()))
                .orderBy(QMember.member.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Member> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }


}
