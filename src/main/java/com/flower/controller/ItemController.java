package com.flower.controller;

import com.flower.constant.BigType;
import com.flower.constant.SmallType;
import com.flower.dto.ItemFormDto;
import com.flower.dto.ItemListDto;
import com.flower.dto.ItemSearchDto;
import com.flower.dto.SessionUser;
import com.flower.entity.Item;
import com.flower.entity.Member;
import com.flower.service.ItemService;
import com.flower.service.MemberService;
import com.flower.service.ValidService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ValidService validService;
    private final MemberService memberService;

    @GetMapping(value = "/admin/item/new")
    public String itemFrom(Model model){
        model.addAttribute("itemFormDto",new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){
        if (bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }
        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId")Long itemId, Model model){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
            model.addAttribute("smallType",itemFormDto.getSmallType());
            model.addAttribute("discountRate",itemFormDto.getDiscountRate());
        }
        catch (EntityNotFoundException e){
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model){
        if (bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫 번째 상품 이미지는 필수 입력값 입니다.");
            return "item/itemForm";
        }
        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        }
        catch (Exception e){
            model.addAttribute("errorMessage","상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/admin/items";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }

    @DeleteMapping(value = "/admin/item/delete/{itemId}")
    public @ResponseBody ResponseEntity deleteItem(@PathVariable("itemId") Long itemId, Principal principal){
        String userEmail = validService.principalEmail(principal);
        if (!itemService.validateItem(userEmail)){
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        itemService.deleteItem(itemId);
        return new ResponseEntity<Long>(itemId, HttpStatus.OK);
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId")Long itemId, Principal principal, HttpSession httpSession){
        String email = "";
        boolean check = false;
        if (principal != null) {
            email = validService.principalEmail(principal);
            Member member = memberService.memberPrint(email);
            check = itemService.pickCheck(itemId, member);
            model.addAttribute("memberEmail",member.getEmail());
            model.addAttribute("memberName",member.getName());
        }
        ItemFormDto itemFormDto = itemService.getChangeItemDtl(itemId);

        ItemListDto itemListDto = new ItemListDto(itemFormDto);
        List<ItemListDto> recentItems = (List<ItemListDto>) httpSession.getAttribute("recentItems");
        if (recentItems == null) {
            recentItems = new ArrayList<>();
        }
        boolean listCheck = false;
        for (ItemListDto itemListDto1 : recentItems){
            if (itemFormDto.getId().equals(itemListDto1.getId())){
                listCheck = true;
                break;
            }
        }
        if (!listCheck){
            recentItems.add(0, itemListDto);
        }

        if (recentItems.size() > 3){
            recentItems = recentItems.subList(0, 3);
        }
        httpSession.setAttribute("recentItems",recentItems);

        model.addAttribute("check",check);
        model.addAttribute("who",email);
        model.addAttribute("item",itemFormDto);
        return "item/itemDtl";
    }

    @PatchMapping(value = "item/pick/{itemId}")
    public @ResponseBody ResponseEntity pick(@PathVariable("itemId")Long itemId, Principal principal){
        if (principal == null){
            return new ResponseEntity<String>("로그인 후 이용해주세요.", HttpStatus.FORBIDDEN);
        }
        String email = validService.principalEmail(principal);
        try{
            itemService.memberAddPick(itemId,email);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(itemId, HttpStatus.OK);
    }

    @PatchMapping(value = "item/pickCancel/{itemId}")
    public @ResponseBody ResponseEntity pickCancel(@PathVariable("itemId")Long itemId, Principal principal){
        if (principal == null){
            return new ResponseEntity<String>("로그인 후 이용해주세요.", HttpStatus.FORBIDDEN);
        }
        String email = validService.principalEmail(principal);
        try{
            itemService.memberCancelPick(itemId,email);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(itemId, HttpStatus.OK);
    }

    @PostMapping(value = "/item/newComment")
    public @ResponseBody ResponseEntity newComment(@RequestBody Map<String, Object> data, Principal principal){
        if (principal == null){
            return new ResponseEntity<String>("로그인 후 이용해주세요.", HttpStatus.FORBIDDEN);
        }
        Long id;
        try{
            Member member = memberService.memberPrint(validService.principalEmail(principal));
            id = itemService.newComment(member,data);
        }
        catch (Exception e){
            return new ResponseEntity<String>("게시글 등록 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(id,HttpStatus.OK);
    }

    @PostMapping(value = "item/modifyComment")
    public @ResponseBody ResponseEntity modifyComment(@RequestBody Map<String, Object> data){
        Long id;
        try{
            id = itemService.commentModify(data);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(id, HttpStatus.OK);
    }

    @DeleteMapping(value = "item/deleteComment")
    public @ResponseBody ResponseEntity deleteComment(@RequestBody Map<String, Object> data){
        try{
            itemService.commentDelete(data);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("완료", HttpStatus.OK);
    }

    @GetMapping(value = "/item/list/{where}")
    public String flowerList(Model model, @PathVariable("where") String where){
        List<ItemListDto> itemOne = null;
        List<ItemListDto> itemTwo = null;
        if (where.equals("flower")){
            itemOne = itemService.getSelectItemList(SmallType.SINGLE);
            itemTwo = itemService.getSelectItemList(SmallType.MULTI);
            model.addAttribute("where","꽃");
        }
        if (where.equals("plants")){
            itemOne = itemService.getSelectItemList(SmallType.OPEN);
            itemTwo = itemService.getSelectItemList(SmallType.FOLIAGE);
            model.addAttribute("where","식물");
        }
        if (where.equals("ran")){
            itemOne = itemService.getSelectItemList(SmallType.EAST);
            itemTwo = itemService.getSelectItemList(SmallType.WEST);
            model.addAttribute("where","란");
        }
        if (where.equals("flowery")){
            itemOne = itemService.getSelectItemList(SmallType.GOOD);
            itemTwo = itemService.getSelectItemList(SmallType.WORST);
            model.addAttribute("where","화한");
        }

        model.addAttribute("itemOne",itemOne);
        model.addAttribute("itemTwo",itemTwo);
        return "item/itemList";
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
