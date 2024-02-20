package com.flower.controller;

import com.flower.dto.MemberSearchDto;
import com.flower.dto.OrderHistDto;
import com.flower.entity.Member;
import com.flower.service.MngService;
import com.flower.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class MngController {
    private final MngService mngService;
    private final OrderService orderService;

    @GetMapping(value = {"/members","/members/{page}"})
    public String membersManage(MemberSearchDto memberSearchDto, @PathVariable("page") Optional<Integer> page,
                                Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<Member> members = mngService.getAdminMemberPage(memberSearchDto, pageable);
        model.addAttribute("members", members);
        model.addAttribute("memberSearchDto", memberSearchDto);
        model.addAttribute("maxPage", 5);
        return "member/memberMng";
    }

    @PostMapping(value = "/member/modify")
    public @ResponseBody ResponseEntity modifyUser(@RequestBody Map<String, Object> user){
        try {
            mngService.modifyMember(user);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("성공", HttpStatus.OK);
    }

    @PatchMapping(value = "/item/pick/{itemId}")
    public @ResponseBody ResponseEntity itemPick(@PathVariable("itemId") Long itemId){
        try {
            mngService.itemPick(itemId);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(itemId, HttpStatus.OK);
    }

    @PatchMapping(value = "/item/unPick/{itemId}")
    public @ResponseBody ResponseEntity itemUnPick(@PathVariable("itemId") Long itemId){
        try {
            mngService.itemUnPick(itemId);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(itemId, HttpStatus.OK);
    }

    @GetMapping(value = {"/order","/order/{page}"})
    public String adminOrder(@PathVariable("page")Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getAdminOrderList(pageable);

        model.addAttribute("order",orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return"order/orderMng";
    }



}
