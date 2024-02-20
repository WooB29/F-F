package com.flower.service;

import com.flower.constant.OrderStatus;
import com.flower.dto.OrderDto;
import com.flower.dto.OrderHistDto;
import com.flower.dto.OrderItemDto;
import com.flower.entity.*;
import com.flower.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    private final DeleteItemImgRepository deleteItemImgRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders){
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems){
                ItemImg itemimg;
                DeleteItemImg deleteItemImg;
                OrderItemDto orderItemDto;
                if (orderItem.getItem() != null){
                    itemimg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(),"Y");
                    orderItemDto = new OrderItemDto(orderItem, itemimg.getImgUrl());
                }
                else {
                    deleteItemImg = deleteItemImgRepository.findByDeleteItemIdAndRepImgYn(orderItem.getDeleteItem().getId(), "Y");
                    orderItemDto = new OrderItemDto(orderItem, deleteItemImg.getImgUrl());
                }

                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getAdminOrderList(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orderPage.getContent()) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg;
                DeleteItemImg deleteItemImg;
                OrderItemDto orderItemDto;
                if (orderItem.getItem() != null) {
                    itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                    orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                } else {
                    deleteItemImg = deleteItemImgRepository.findByDeleteItemIdAndRepImgYn(orderItem.getDeleteItem().getId(), "Y");
                    orderItemDto = new OrderItemDto(orderItem, deleteItemImg.getImgUrl());
                }
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtos, pageable, orderPage.getTotalElements());
    }


    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();
        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId, String num){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        if (num.equals("1")){
            order.setOrderStatus(OrderStatus.ORDER);
        }
        if (num.equals("2")){
            order.cancelOrder();
        }
        if (num.equals("3")){
            order.setOrderStatus(OrderStatus.CANCEL_WAIT);
        }
    }

    public void orderChange(Long orderId, OrderStatus orderStatus){
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (orderStatus.equals(OrderStatus.CANCEL)){
            order.cancelOrder();
        }
        else {
            order.setOrderStatus(orderStatus);
        }
        orderRepository.save(order);
    }

    public Long orders(List<OrderDto> orderDtoList, String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList){
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
            orderItemList.add(orderItem);
        }
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

}