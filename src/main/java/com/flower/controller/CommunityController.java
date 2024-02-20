package com.flower.controller;

import com.flower.constant.Role;
import com.flower.dto.*;
import com.flower.entity.Comment;
import com.flower.entity.Member;
import com.flower.entity.Notice;
import com.flower.entity.Qna;
import com.flower.service.CommunityService;
import com.flower.service.MemberService;
import com.flower.service.ValidService;
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
@RequestMapping("/community")
public class CommunityController {
    private final CommunityService communityService;
    private final ValidService validService;
    private final MemberService memberService;
    private final HttpSession httpSession;

    @GetMapping(value = {"/{where}","/{where}/{page}"})
    public String getCommunity(CommunitySearchDto communitySearchDto, @PathVariable("page") Optional<Integer> page,
                         Model model, Principal principal, @PathVariable("where") String where){
        Role write_role = validService.roleCheck(principal);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        Page<BoardFormDto> boardFormDtos = null;
        if (where.equals("notice")){
            boardFormDtos = communityService.getNoticePage(communitySearchDto,pageable);
        }
        if (where.equals("qna")){
            boardFormDtos = communityService.getQnaPage(communitySearchDto,pageable);
        }

        String errorMessage = (String) httpSession.getAttribute("errorMessage");
        if (errorMessage != null){
            model.addAttribute("errorMessage",errorMessage);
            httpSession.removeAttribute("errorMessage");
        }

        model.addAttribute("boards", boardFormDtos);
        model.addAttribute("communitySearchDto", communitySearchDto);
        model.addAttribute("maxPage",10);
        model.addAttribute("write_role",write_role);
        model.addAttribute("where",where);
        return "community/board";
    }

    @GetMapping(value = "/{where}/new")
    public String communityFrom(Model model, Principal principal, @PathVariable("where") String where){
        String email = validService.principalEmail(principal);
        BoardFormDto boardFormDto = new BoardFormDto();
        Member member = memberService.memberPrint(email);
        boardFormDto.setMember(member);
        model.addAttribute("where",where);
        model.addAttribute("boardFormDto", boardFormDto);
        return "community/boardForm";
    }

    @PostMapping(value = "/{where}/new")
    public String communityNew(@Valid BoardFormDto boardFormDto, BindingResult bindingResult, Model model,
                            @RequestParam("boardImgFile") List<MultipartFile> boardImgFileList,
                            Principal principal, @PathVariable("where") String where){
        if (bindingResult.hasErrors()){
            model.addAttribute("where",where);
            return "community/boardForm";
        }
        String email = validService.principalEmail(principal);
        Member member = memberService.memberPrint(email);
        boardFormDto.setMember(member);

        try {
            if (where.equals("notice")){
                communityService.saveNotice(boardFormDto, boardImgFileList);
            }
            if (where.equals("qna")){
                communityService.saveQna(boardFormDto, boardImgFileList);
            }
        }
        catch (Exception e){
            httpSession.setAttribute("errorMessage",e.getMessage());
            return "redirect:/community/"+where;
        }
        return "redirect:/community/"+where;
    }

    @GetMapping(value = "/{where}/view/{id}")
    public String communityView(@PathVariable Long id, Model model, Principal principal, @PathVariable("where") String where){
        String email;
        if (principal == null){
            email = "Guest";
        }
        else {
            email = validService.principalEmail(principal);
        }

        BoardFormDto boardFormDtos = null;
        List<Comment> comments = null;

        if (where.equals("notice")){
            boardFormDtos = communityService.getNoticeDtl(id);
            comments = communityService.getNoticeCommentList(id);
            communityService.updateHits(id);
        }
        if (where.equals("qna")){
            boardFormDtos = communityService.getQnaDtl(id);
            comments = communityService.getQnaCommentList(id);
            communityService.updateHitsQna(id);
        }

        model.addAttribute("who",email);
        model.addAttribute("Comments", comments);
        model.addAttribute("where",where);
        model.addAttribute("boardFormDto", boardFormDtos);
        model.addAttribute("readonly",true);
        return "community/boardForm";
    }

    @GetMapping(value = "/{where}/modify/{id}")
    public String communityModify(@PathVariable Long id, Model model, CommentDto commentDto,
                               @PathVariable("where") String where){
        BoardFormDto boardFormDto = null;
        if (where.equals("notice")){
            boardFormDto = communityService.getNoticeDtl(id);
        }
        if (where.equals("qna")){
            boardFormDto = communityService.getQnaDtl(id);
        }

        model.addAttribute("CommentDto", commentDto);
        model.addAttribute("where",where);
        model.addAttribute("boardFormDto",boardFormDto);
        model.addAttribute("readonly",false);
        return "community/boardForm";
    }

    @PostMapping(value = "/{where}/modify/{id}")
    public String communityModify(@Valid BoardFormDto boardFormDto, BindingResult bindingResult, Model model,
                               @RequestParam("boardImgFile") List<MultipartFile> boardImgFileList,
                               Principal principal, @PathVariable("where") String where){
        if (bindingResult.hasErrors()){
            model.addAttribute("where",where);
            return "community/boardForm";
        }

        String email = validService.principalEmail(principal);

        try {
            Member member = memberService.memberPrint(email);
            boardFormDto.setMember(member);
            if (where.equals("notice")) {
                communityService.updateNotice(boardFormDto, boardImgFileList);
            }
            if (where.equals("qna")) {
                communityService.updateQna(boardFormDto, boardImgFileList);
            }
        }
        catch (Exception e){
            httpSession.setAttribute("errorMessage", e.getMessage());
            return "redirect:/community/"+where;
        }

        return "redirect:/community/"+where;
    }

    @GetMapping(value = "/{where}/delete/{id}")
    public String communityDelete(@PathVariable Long id, Principal principal,
                                  @PathVariable("where") String where){
        String email;
        if (principal == null){
            httpSession.setAttribute("errorMessage","권한이 없습니다.");
            return "redirect:/community/"+where;
        }
        email = validService.principalEmail(principal);

        boolean check =false;
        if (where.equals("notice")) {
            check = communityService.deleteNotice(id, email);
        }
        if (where.equals("qna")) {
            check = communityService.deleteQna(id,email);
        }

        if (!check){
            httpSession.setAttribute("errorMessage","회원정보가 일치하지 않습니다.1");
        }

        return "redirect:/community/"+where;
    }

    @PostMapping(value = "/newComment")
    public @ResponseBody ResponseEntity newComment(@RequestBody Map<String, Object> data, Principal principal){
        if (principal == null){
            return new ResponseEntity<String>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }
        String email = validService.principalEmail(principal);
        Member member = memberService.memberPrint(email);
        List<CommentDto> list;
        try{
            list = communityService.newComment(member,data);
        }
        catch (Exception e){
            return new ResponseEntity<String>("등록중 오류발생", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping(value = "/commentDelete")
    public @ResponseBody ResponseEntity deleteComment(@RequestBody Map<String, Object> data){
        try {
            communityService.commentDelete(data);
        }
        catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("완료", HttpStatus.OK);
    }

    @PostMapping(value = "/commentModify")
    public @ResponseBody ResponseEntity modifyComment(@RequestBody Map<String, Object> data){
        Long id;
        try{
            id = communityService.commentModify(data);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(id, HttpStatus.OK);
    }

    @PostMapping(value = "/addComment")
    public @ResponseBody ResponseEntity addComment(@RequestBody Map<String, Object> data, Principal principal){
        if (principal == null){
            return new ResponseEntity<String>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }
        String email = validService.principalEmail(principal);
        Member member = memberService.memberPrint(email);
        Long id;
        try{
            id = communityService.addComment(member,data);
        }
        catch (Exception e){

            return new ResponseEntity<String>("등록중 오류발생", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @DeleteMapping(value = "/commentChildDelete")
    public @ResponseBody ResponseEntity commentChildDelete(@RequestBody Map<String, Object> data){
        try {
            communityService.commentChildDelete(data);
        }
        catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("완료", HttpStatus.OK);
    }

    @PostMapping(value = "/commentChildModify")
    public @ResponseBody ResponseEntity commentChildModify(@RequestBody Map<String, Object> data){
        Long id;
        try{
            id = communityService.commentChildModify(data);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
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