package com.flower.dto;


import com.flower.constant.BigType;
import com.flower.constant.ItemSellStatus;
import com.flower.constant.SmallType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private BigType searchBigType;

    private String searchBy;

    private String searchQuery = "";
}
