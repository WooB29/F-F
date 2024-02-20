package com.flower.dto;

import com.flower.constant.BigType;
import com.flower.constant.ItemSellStatus;
import com.flower.constant.SmallType;
import com.flower.entity.Comment;
import com.flower.entity.Item;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력값입니다.")
    private Integer price;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력값입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private BigType bigType;

    @NotNull(message = "소 분류는 필수 입력값입니다.")
    private SmallType smallType;

    private int hits;

    @DecimalMin(value = "1", message = "할인율은 1이상이어야 합니다.")
    @DecimalMax(value = "99", message = "할인율은 99이하여야 합니다.")
    private Integer discountRate;

    private Integer discountPrice;

    private String bigName;
    private String smallName;

    private List<Comment> comments;

    //-------------------------------------------------------------------------------------------

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    private List<Long> itemImgIds = new ArrayList<>();

    //-------------------------------------------------------------------------------------------

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }
    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
    }

    public ItemFormDto changeName(ItemFormDto itemFormDto){
        if (itemFormDto.bigType.equals(BigType.FLOWER)){
            itemFormDto.bigName="꽃";
            if (itemFormDto.smallType.equals(SmallType.MULTI)){
                itemFormDto.smallName="꽃 바구니";
            }
            else {
                itemFormDto.smallName="꽃 다발";
            }
        }
        if (itemFormDto.bigType.equals(BigType.PLANTS)){
            itemFormDto.bigName="식물";
            if (itemFormDto.smallType.equals(SmallType.OPEN)){
                itemFormDto.smallName="개업 식물";
            }
            else {
                itemFormDto.smallName="관엽 식물";
            }
        }
        if (itemFormDto.bigType.equals(BigType.RAN)){
            itemFormDto.bigName="란";
            if (itemFormDto.smallType.equals(SmallType.EAST)){
                itemFormDto.smallName="동양란";
            }
            else {
                itemFormDto.smallName="서양란";
            }
        }
        if (itemFormDto.bigType.equals(BigType.FLOWERY)){
            itemFormDto.bigName="화한";
            if (itemFormDto.smallType.equals(SmallType.GOOD)){
                itemFormDto.smallName="축하 화한";
            }
            else {
                itemFormDto.smallName="근조 화한";
            }
        }
        return itemFormDto;
    }

}

