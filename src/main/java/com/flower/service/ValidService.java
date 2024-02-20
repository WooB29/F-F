package com.flower.service;

import com.flower.constant.Role;
import com.flower.dto.SessionUser;
import com.flower.entity.Member;
import com.flower.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ValidService {
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    public Map<String, String> validateHandling(BindingResult bindingResult){
        Map<String, String> validatorResult = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()){
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }

    public String principalEmail(Principal principal){
        if (httpSession.getAttribute("user") != null) {
            return ((SessionUser)httpSession.getAttribute("user")).getEmail();
        }
        return principal.getName();
    }

    public Role roleCheck(Principal principal){
        if (principal == null){
            return Role.GUEST;
        }
        String email = principalEmail(principal);
        Member member = memberRepository.findByEmail(email).orElseThrow();
        return member.getRole();
    }

    public boolean validCheck(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        if (member.getRole().equals(Role.ADMIN)){
            return true;
        }
        return false;
    }


}
