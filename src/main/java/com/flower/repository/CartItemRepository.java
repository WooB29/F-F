package com.flower.repository;

import com.flower.dto.CartDetailDto;
import com.flower.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    List<CartItem> findByItemId(Long itemId);

    @Query("select new com.flower.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl, " +
            "i.stockNumber ,i.discountRate, i.discountPrice, i.bigType, i.smallType)" +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repImgYn = 'Y' " +
            "order by ci.regTime desc")
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
