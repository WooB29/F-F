package com.flower.controller;

import com.flower.constant.ErrorStatus;
import com.flower.dto.ErrorDto;
import com.flower.service.MngService;
import com.flower.service.ValidService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ErrorController {
    private final MngService mngService;
    private final ValidService validService;

    @PostMapping(value = "/error/err")
    public @ResponseBody ResponseEntity errorSave(@RequestBody Map<String, Object> err){
        try{
            mngService.errSave(err);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("성공", HttpStatus.OK);
    }

    @GetMapping(value = {"/error/err" , "/error/err/{page}"})
    public String adminError(@PathVariable("page") Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<ErrorDto> errorDto = mngService.getAdminErrorList(pageable);

        model.addAttribute("error",errorDto);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return"error/error";
    }

    @PatchMapping(value = "/error/{errorId}/change/{errorStatus}")
    public @ResponseBody ResponseEntity errorChange(@PathVariable("errorId") Long errorId,
                                                    @PathVariable("errorStatus") ErrorStatus errorStatus, Principal principal){
        boolean adminCheck = validService.validCheck(validService.principalEmail(principal));
        if (!adminCheck){
            return new ResponseEntity<String>("주문취소 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        try {
            mngService.errorChange(errorId, errorStatus);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(errorId, HttpStatus.OK);
    }
}
