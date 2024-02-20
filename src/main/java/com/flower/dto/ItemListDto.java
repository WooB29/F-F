package com.flower.dto;

import com.flower.constant.BigType;
import com.flower.constant.ItemPick;
import com.flower.constant.SmallType;
import com.flower.entity.Item;
import com.flower.entity.ItemImg;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemListDto {
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;
    private Integer discountRate;
    private Integer discountPrice;
    private String bigType;
    private String smallType;
    private ItemPick itemPick;

    @QueryProjection
    public ItemListDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price,Integer discountRate,
                        Integer discountPrice, BigType bigType, SmallType smallType, ItemPick itemPick) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.itemPick = itemPick;
        nameChange(bigType, smallType);
    }

    public ItemListDto (ItemFormDto itemFormDto){
        this.id = itemFormDto.getId();
        this.itemNm = itemFormDto.getItemNm();
        for (ItemImgDto itemImgDto : itemFormDto.getItemImgDtoList()){
            if (itemImgDto.getRepImgYn().equals("Y")){
                this.imgUrl = itemImgDto.getImgUrl();
            }
        }
    }

    public ItemListDto(Item item, ItemImg itemImg){
        this.id = item.getId();
        this.itemNm = item.getItemNm();
        this.itemDetail = item.getItemDetail();
        this.imgUrl = itemImg.getImgUrl();
        this.price = item.getPrice();
        this.discountRate = item.getDiscountRate();
        this.discountPrice = item.getDiscountPrice();
        this.itemPick = item.getItemPick();
        nameChange(item.getBigType(), item.getSmallType());
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