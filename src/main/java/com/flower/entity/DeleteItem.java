package com.flower.entity;

import com.flower.constant.BigType;
import com.flower.constant.ItemPick;
import com.flower.constant.ItemSellStatus;
import com.flower.constant.SmallType;
import com.flower.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="deleteItem")
@Getter
@Setter
@ToString
public class DeleteItem extends BaseEntity{
    @Id
    @Column(name="deleteItem_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false,length = 50)
    private String itemNm;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private BigType bigType;

    @Enumerated(EnumType.STRING)
    private SmallType smallType;

    @Column
    private Integer discountRate;

    @Column
    private Integer discountPrice;

    public DeleteItem(){};

    public DeleteItem(Item item){
        this.itemNm = item.getItemNm();
        this.price = item.getPrice();
        this.bigType = item.getBigType();
        this.smallType = item.getSmallType();
        this.discountRate = item.getDiscountRate();
        this.discountPrice = item.getDiscountPrice();
    }
}