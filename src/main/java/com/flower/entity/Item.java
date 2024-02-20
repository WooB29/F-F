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
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false,length = 50)
    private String itemNm;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    @Enumerated(EnumType.STRING)
    private BigType bigType;
    private String bigTypeName;

    @Enumerated(EnumType.STRING)
    private SmallType smallType;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int hits;

    @Column
    private Integer discountRate;

    @Column
    private Integer discountPrice;

    @Column
    private ItemPick itemPick;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "member_item",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Member> member;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.discountRate = itemFormDto.getDiscountRate();
        this.discountPrice = itemFormDto.getDiscountPrice();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if (restStock<0){
            throw new OptimisticLockException("상품의 재고가 부족합니다.(현재 재고 수량 : "+this.stockNumber+")");
        }
        if (restStock==0){
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
        this.stockNumber = restStock;
        this.hits += restStock;
    }

    public int stockCheck(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if (restStock<0){
            throw new OptimisticLockException("상품의 재고가 부족합니다.(현재 재고 수량 : "+this.stockNumber+")");
        }

        return stockNumber;
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
        this.hits -= stockNumber;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }

}