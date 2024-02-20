package com.flower.dto;

import com.flower.constant.BigType;
import com.flower.constant.SmallType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailDto {
    private Long cartItemId;
    private String itemNm;
    private int price;
    private int count;
    private String imgUrl;
    private int stockNumber;
    private Integer discountRate;
    private Integer discountPrice;
    private String smallType;
    private String bigType;

    public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl, int stockNumber,
                         Integer discountRate, Integer discountPrice, BigType bigType, SmallType smallType) {
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
        this.stockNumber = stockNumber;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        nameChange(bigType, smallType);
    }

    private void nameChange(BigType bigType, SmallType smallType){
        if (bigType.equals(BigType.FLOWER)){
            this.bigType = "꽃";
            if (smallType.equals(SmallType.MULTI)){
                this.smallType = "꽃 바구니";
            }
            else {
                this.smallType = "꽃 다발";
            }
        }
        if (bigType.equals(BigType.PLANTS)){
            this.bigType = "식물";
            if (smallType.equals(SmallType.OPEN)){
                this.smallType = "개업 식물";
            }
            else {
                this.smallType = "관엽 식물";
            }
        }
        if (bigType.equals(BigType.RAN)){
            this.bigType = "란";
            if (smallType.equals(SmallType.EAST)){
                this.smallType = "동양란";
            }
            else {
                this.smallType = "서양란";
            }
        }
        if (bigType.equals(BigType.FLOWERY)){
            this.bigType = "화한";
            if (smallType.equals(SmallType.GOOD)){
                this.smallType = "축하 화한";
            }
            else {
                this.smallType = "근조 화한";
            }
        }
    }
}
