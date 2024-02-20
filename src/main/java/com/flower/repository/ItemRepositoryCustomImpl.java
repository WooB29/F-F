package com.flower.repository;

import com.flower.constant.BigType;
import com.flower.constant.ItemPick;
import com.flower.constant.ItemSellStatus;
import com.flower.constant.SmallType;
import com.flower.dto.ItemListDto;
import com.flower.dto.ItemSearchDto;
import com.flower.dto.QItemListDto;
import com.flower.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ?
                null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression searchBigTypeEq(BigType searchBigType){
        return searchBigType == null ?
                null : QItem.item.bigType.eq(searchBigType);
    }

    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all",searchDateType) || searchDateType == null){
            return null;
        }
        else if (StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        }
        else if (StringUtils.equals("1w",searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }
        else if (StringUtils.equals("1m",searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }
        else if (StringUtils.equals("6m",searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }


    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if (StringUtils.equals("id",searchBy)){
            return QItem.item.id.like("%"+searchQuery+"%");
        }
        if (StringUtils.equals("itemNm",searchBy)){
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        }

        return null;
    }
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchBigTypeEq(itemSearchDto.getSearchBigType()),
                        searchByLike(itemSearchDto.getSearchBy(),itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Item> content = results.getResults();
        for (Item item : content){
            if (item.getBigType().equals(BigType.FLOWER)){
                item.setBigTypeName("꽃");
            }
            if (item.getBigType().equals(BigType.PLANTS)){
                item.setBigTypeName("식물");
            }
            if (item.getBigType().equals(BigType.RAN)){
                item.setBigTypeName("란");
            }
            if (item.getBigType().equals(BigType.FLOWERY)){
                item.setBigTypeName("화한");
            }
        }
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%"+searchQuery+"%");
    }


    public Page<ItemListDto> getItemListPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        QueryResults<ItemListDto> results = queryFactory.select(new QItemListDto(item.id, item.itemNm,
                        item.itemDetail,itemImg.imgUrl,item.price,item.discountRate,item.discountPrice,item.bigType,item.smallType,item.itemPick))
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<ItemListDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }



    public List<ItemListDto> getSelectItemList(SmallType smallType){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        List<ItemListDto> results = queryFactory.select(new QItemListDto(item.id, item.itemNm,
                        item.itemDetail,itemImg.imgUrl,item.price,item.discountRate,item.discountPrice,item.bigType,item.smallType,item.itemPick))
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(item.smallType.eq(smallType))
                .orderBy(item.id.desc()).fetch();
        return results;
    }

    public List<ItemListDto> getBannerItem(){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        List<ItemListDto> results = queryFactory.select(new QItemListDto(item.id, item.itemNm,
                    item.itemDetail,itemImg.imgUrl,item.price,item.discountRate,item.discountPrice,item.bigType,item.smallType,item.itemPick))
                .from(itemImg).join(itemImg.item, item).
                where(itemImg.repImgYn.eq("Y").and(item.itemPick.eq(ItemPick.PICK)))
                .orderBy(item.id.desc())
                .fetch();
        return results;
    }



    public List<ItemListDto> getBestItem(){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        List<ItemListDto> results = queryFactory.select(new QItemListDto(item.id, item.itemNm,
                    item.itemDetail,itemImg.imgUrl,item.price,item.discountRate,item.discountPrice,item.bigType,item.smallType,item.itemPick))
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .orderBy(item.hits.desc()).limit(10)
                .fetch();
        return results;
    }

    public List<Comment> getItemDetailComment(Long itemId){
        List<Comment> results = queryFactory.selectFrom(QComment.comment)
                .leftJoin(QItem.item.comments, QComment.comment)
                .fetchJoin()
                .where(QItem.item.id.eq(itemId))
                .fetch();
        return results;
    }

}
