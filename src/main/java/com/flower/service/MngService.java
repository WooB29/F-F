package com.flower.service;

import com.flower.constant.ErrorStatus;
import com.flower.constant.ItemPick;
import com.flower.constant.Role;
import com.flower.dto.ErrorDto;
import com.flower.dto.MemberSearchDto;
import com.flower.entity.*;
import com.flower.entity.Error;
import com.flower.repository.ErrorRepository;
import com.flower.repository.ItemRepository;
import com.flower.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MngService {
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ErrorRepository errorRepository;

    public Page<Member> getAdminMemberPage(MemberSearchDto memberSearchDto, Pageable pageable){
        return memberRepository.getAdminMemberPage(memberSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public boolean validateMember(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        if (member.getRole().equals(Role.ADMIN)){
            return true;
        }
        return false;
    }

    public void modifyMember(Map<String, Object> user){
        String email = (String) user.get("email");
        String name = (String) user.get("name");
        String stringRole = (String) user.get("role");
        Role role;
        if (stringRole.equals("USER")){
             role=Role.USER;
        }
        else {
            role=Role.ADMIN;
        }

        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        member.modify(name,role);
        memberRepository.save(member);
    }

    public void itemPick(Long itemId){
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.setItemPick(ItemPick.PICK);
        itemRepository.save(item);
    }

    public void itemUnPick(Long itemId){
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.setItemPick(ItemPick.UNPICK);
        itemRepository.save(item);
    }

    public void errSave(Map<String, Object> err){
        String email = (String) err.get("email");
        int code = (Integer) err.get("code");
        String content = (String) err.get("content");
        String location = (String) err.get("location");
        String comment = (String) err.get("comment");
        Error error = new Error(email,code,content,location,comment, ErrorStatus.ERR_YET);
        errorRepository.save(error);
    }

    @Transactional(readOnly = true)
    public Page<ErrorDto> getAdminErrorList(Pageable pageable) {
        Page<Error> errorPage = errorRepository.findAll(pageable);
        List<ErrorDto> errorDtoList = new ArrayList<>();

        for (Error error : errorPage.getContent()) {
            Member member = memberRepository.findByEmail(error.getEmail()).orElseThrow();
            ErrorDto errorDto = new ErrorDto(error,member);
            errorDtoList.add(errorDto);
        }

        return new PageImpl<>(errorDtoList, pageable, errorPage.getTotalElements());
    }

    public void errorChange(Long errorId, ErrorStatus errorStatus){
        Error error = errorRepository.findById(errorId).orElseThrow();
        error.setErrorStatus(errorStatus);
        errorRepository.save(error);
    }




}
