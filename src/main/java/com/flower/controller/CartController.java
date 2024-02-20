package com.flower.controller;

import com.flower.dto.CartDetailDto;
import com.flower.dto.CartItemDto;
import com.flower.dto.CartOrderDto;
import com.flower.dto.ItemListDto;
import com.flower.service.CartService;
import com.flower.service.ValidService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ValidService validService;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = validService.principalEmail(principal);
        Long cartItemId;
        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        }
        catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        String email = validService.principalEmail(principal);
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(email);
        model.addAttribute("memberEmail",email);
        model.addAttribute("cartItems", cartDetailDtoList);
        return "cart/cartList";
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal){
        String email = validService.principalEmail(principal);
        if (count <= 0){
            return new ResponseEntity<String>("최소 1개이상 담아주세요",HttpStatus.BAD_REQUEST);
        }
        else if (!cartService.validateCartItem(cartItemId, email)){
            return new ResponseEntity<String>("수정권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        cartService.updateCartItemCount(cartItemId,count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       Principal principal){
        String email = validService.principalEmail(principal);
        if (!cartService.validateCartItem(cartItemId, email)){
            return new ResponseEntity<String>("수정권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                      Principal principal){
        String email = validService.principalEmail(principal);
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요.",HttpStatus.FORBIDDEN);
        }
        for (CartOrderDto cartOrder : cartOrderDtoList){
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), email)){
                return new ResponseEntity<String>("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }
        Long orderId;
        try {
            orderId = cartService.orderCartItem(cartOrderDtoList, email);
        }
        catch (Exception e){
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
