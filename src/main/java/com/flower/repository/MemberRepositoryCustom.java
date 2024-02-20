package com.flower.repository;

import com.flower.dto.MemberSearchDto;
import com.flower.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> getAdminMemberPage(MemberSearchDto memberSearchDto, Pageable pageable);
}
