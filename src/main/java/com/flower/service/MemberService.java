package com.flower.service;

import com.flower.config.CustomUserDetails;
import com.flower.constant.Role;
import com.flower.dto.MemberEditDto;
import com.flower.entity.Member;
import com.flower.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public void idCheck(String email){
        memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public Member saveMember(Member member){
        return memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        String password = member.getPassword();
        String displayName = member.getName();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getRole().equals(Role.USER)){
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        if (member.getRole().equals(Role.ADMIN)){
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }


        return new CustomUserDetails(email, password, authorities, displayName);
    }

    public Member memberPrint(String email){
        return memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public Member memberModify(MemberEditDto memberEditDto, String email){
        Member member = memberPrint(email);
        member.memberModify(memberEditDto);
        return memberRepository.save(member);
    }

    public Long passCheck(Map<String, Object> data, String email){
        Member member = memberPrint(email);
        String pass = (String) data.get("pass");
        if (passwordEncoder.matches(pass, member.getPassword())){
            return member.getId();
        }
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    public Long passChange(Map<String, Object> data, String email){
        Member member = memberPrint(email);
        String pass = (String) data.get("pass");
        String encode = passwordEncoder.encode(pass);
        member.setPassword(encode);
        memberRepository.save(member);
        return member.getId();
    }





}
