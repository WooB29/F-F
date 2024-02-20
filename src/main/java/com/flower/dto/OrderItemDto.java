package com.flower.dto;

import com.flower.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long id;
    private String itemNm;
    private int count;
    private int orderPrice;
    private Integer discountRate;
    private Integer discountPrice;
    private String imgUrl;
    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        if (orderItem.getItem() != null){
            this.id = orderItem.getItem().getId();
            this.itemNm = orderItem.getItem().getItemNm();
            this.count = orderItem.getCount();
            this.orderPrice = orderItem.getOrderPrice();
            this.imgUrl = imgUrl;
            this.discountRate = orderItem.getItem().getDiscountRate();
            this.discountPrice = orderItem.getItem().getDiscountPrice();
        }
        else {
            this.id = orderItem.getDeleteItem().getId();
            this.itemNm = orderItem.getDeleteItem().getItemNm();
            this.count = orderItem.getCount();
            this.orderPrice = orderItem.getOrderPrice();
            this.imgUrl = imgUrl;
            this.discountRate = orderItem.getDeleteItem().getDiscountRate();
            this.discountPrice = orderItem.getDeleteItem().getDiscountPrice();
        }

    }
}