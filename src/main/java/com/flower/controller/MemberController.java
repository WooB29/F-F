package com.flower.controller;

import com.flower.dto.*;
import com.flower.entity.Member;
import com.flower.service.ApiService;
import com.flower.service.ItemService;
import com.flower.service.MemberService;
import com.flower.service.ValidService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final ValidService validService;
    private final ItemService itemService;
    private final ApiService apiService;

    @GetMapping(value = "/login")
    public String memberLogin(Model model){
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "member/memberForm";
    }
    @PostMapping(value = "/idCheck")
    public @ResponseBody int idCheck(@RequestBody Map<String, Object> check){
        try {
            memberService.idCheck((String) check.get("mail"));
        }
        catch (EntityNotFoundException e){
            return 0;
        }
        return 1;
    }

    @PostMapping(value = "/new")
    public @ResponseBody ResponseDto<?> signup(@Valid @RequestBody MemberFormDto memberFormDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            Map<String, String> validatorResult = validService.validateHandling(bindingResult);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), validatorResult);
        }
        if (memberFormDto.getCheck() == 0){
            return new ResponseDto<>(HttpStatus.CONFLICT.value(), 0);
        }
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        memberService.saveMember(member);
        return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
    }

    @GetMapping(value = "/loginSuccess")
    public @ResponseBody String loginSuccess(){
        return "/";
    }

    @GetMapping(value = "/loginFail")
    public @ResponseBody String loginFail(){
        return "아이디 비밀번호가 일치하지 않습니다.";
    }

    @GetMapping(value = "/pick")
    public String membersPick(Principal principal, Model model){
        String email = validService.principalEmail(principal);
        List<ItemListDto> itemListDto = itemService.getPickItem(email);
        model.addAttribute("pickItems",itemListDto);
        return "member/memberPick";
    }

    @GetMapping(value = "/modify")
    public String memberModify(Principal principal, Model model){
        String email = validService.principalEmail(principal);
        MemberEditDto memberEditDto = new MemberEditDto(memberService.memberPrint(email));
        model.addAttribute("memberEditDto",memberEditDto);
        return "member/memberEdit";
    }

    @PostMapping(value = "/modify")
    public String modifyMember(@Valid MemberEditDto memberEditDto, BindingResult bindingResult,
                               Principal principal, Model model){
        if (bindingResult.hasErrors()){
            return "member/memberEdit";
        }
        try {
            String email = validService.principalEmail(principal);
            memberService.memberModify(memberEditDto, email);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberEdit";
        }
        return "redirect:/";
    }

    @PostMapping(value = "/passCheck")
    public @ResponseBody ResponseEntity passCheck(@RequestBody Map<String, Object> data, Principal principal){
        String email = validService.principalEmail(principal);
        Long id;
        try {
            id = memberService.passCheck(data, email);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(id,HttpStatus.OK);
    }

    @GetMapping(value = "/changePass")
    public String changePass(){
        return "member/memberPassEdit";
    }

    @PostMapping(value = "/changePass")
    public @ResponseBody ResponseEntity passChange(@RequestBody Map<String, Object> data, Principal principal){
        String email = validService.principalEmail(principal);
        Long id;
        try {
            id = memberService.passChange(data, email);
        }
        catch (Exception e){
            return  new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(id, HttpStatus.OK);
    }

    @ModelAttribute("recentItems")
    public List<ItemListDto> getRecentItems(HttpSession httpSession){

        List<ItemListDto> recentItems = (List<ItemListDto>) httpSession.getAttribute("recentItems");
        if (recentItems == null){
            recentItems = new ArrayList<>();
        }
        return recentItems;
    }

}
