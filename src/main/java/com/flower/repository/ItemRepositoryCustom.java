package com.flower.repository;

import com.flower.constant.SmallType;
import com.flower.dto.ItemListDto;
import com.flower.dto.ItemSearchDto;
import com.flower.entity.Comment;
import com.flower.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<ItemListDto> getItemListPage(ItemSearchDto itemSearchDto, Pageable pageable);

    List<ItemListDto> getSelectItemList(SmallType smallType);

    List<ItemListDto> getBestItem();

    List<ItemListDto> getBannerItem();

    List<Comment> getItemDetailComment(Long itemId);
}
