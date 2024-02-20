package com.flower.controller;

import com.flower.constant.OrderStatus;
import com.flower.dto.ItemListDto;
import com.flower.dto.OrderDto;
import com.flower.dto.OrderHistDto;
import com.flower.service.MngService;
import com.flower.service.OrderService;
import com.flower.service.ValidService;
import com.siot.IamportRestClient.IamportClient;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ValidService validService;

    @PostMapping(value = "/order")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = validService.principalEmail(principal);
        Long orderId;
        try{
            orderId = orderService.order(orderDto, email);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model){
        String email = validService.principalEmail(principal);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(email, pageable);

        model.addAttribute("orders",orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping(value = "/order/{orderId}/cancel/{num}")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, @PathVariable("num") String num, Principal principal){
        String email = validService.principalEmail(principal);
        if (!orderService.validateOrder(orderId, email)){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        try {
            orderService.cancelOrder(orderId, num);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @PatchMapping(value = "/order/{orderId}/change/{OrderStatus}")
    public @ResponseBody ResponseEntity orderChange(@PathVariable("orderId") Long orderId,
                                                    @PathVariable("OrderStatus") OrderStatus orderStatus, Principal principal){
        boolean adminCheck = validService.validCheck(validService.principalEmail(principal));
        if (!adminCheck){
            return new ResponseEntity<String>("주문취소 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        try {
            orderService.orderChange(orderId, orderStatus);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
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
