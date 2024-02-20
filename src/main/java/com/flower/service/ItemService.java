package com.flower.service;

import com.flower.constant.BigType;
import com.flower.constant.OrderStatus;
import com.flower.constant.Role;
import com.flower.constant.SmallType;
import com.flower.dto.*;
import com.flower.entity.*;
import com.flower.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final DeleteItemRepository deleteItemRepository;
    private final DeleteItemImgRepository deleteItemImgRepository;



    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = itemFormDto.createItem();

        itemRepository.save(item);
        for (int i =0; i<itemImgFileList.size(); i++){
            ItemImg itemimg = new ItemImg();
            itemimg.setItem(item);
            if (i==0)
                itemimg.setRepImgYn("Y");
            else
                itemimg.setRepImgYn("N");
            itemImgService.saveItemImg(itemimg, itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemimgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemimg : itemimgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemimg);
            itemImgDtoList.add(itemImgDto);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    @Transactional(readOnly = true)
    public ItemFormDto getChangeItemDtl(Long itemId){
        List<ItemImg> itemimgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemimg : itemimgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemimg);
            itemImgDtoList.add(itemImgDto);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        itemFormDto.changeName(itemFormDto);
        //itemFormDto.setComments(itemRepository.getItemDetailComment(itemId));

        return itemFormDto;
    }

    @Transactional(readOnly = true)
    public boolean pickCheck(Long itemId, Member member){
        List<Long> itemsId = member.getItemsId();
        if (itemsId==null){
            return false;
        }
        for (Long item : itemsId){
            if (item.equals(itemId)){
                return true;
            }
        }
        return false;
    }

    public void memberAddPick(Long itemId, String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        List<Long> itemsId = member.getItemsId();
        if (itemsId == null){
            itemsId = new ArrayList<>();
            member.setItemsId(itemsId);
        }
        else {
            itemsId.add(itemId);
        }
        memberRepository.save(member);
    }

    public void memberCancelPick(Long itemId, String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        List<Long> itemsId = member.getItemsId();

        for (Long items : itemsId){
            if (items.equals(itemId)){
                itemsId.remove(items);
                break;
            }
        }
        member.setItemsId(itemsId);
        memberRepository.save(member);
    }


    public Long newComment(Member member, Map<String, Object> data){
        String content = (String) data.get("content");
        CommentDto commentDto = CommentDto.create(content, member);
        Comment comment = Comment.createComment(commentDto);
        commentRepository.save(comment);
        Long itemId = Long.parseLong((String) data.get("itemId"));
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.addComment(comment);
        itemRepository.save(item);
        return itemId;
    }

    public Long commentModify(Map<String, Object> data){
        String content = (String) data.get("content");
        Long commentId = ((Integer) data.get("commentId")).longValue();

        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setContent(content);
        commentRepository.save(comment);
        return comment.getId();
    }

    public void commentDelete(Map<String, Object> data){
        Long commentId = ((Integer) data.get("commentId")).longValue();
        Long itemId = Long.parseLong((String) data.get("itemId"));
        Item item = itemRepository.findById(itemId).orElseThrow();
        List<Comment> commentList = item.getComments();
        for (Comment comment : commentList){
            if (comment.getId().equals(commentId)){
                commentList.remove(comment);
                break;
            }
        }
        itemRepository.save(item);
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        for (int i =0; i<itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public boolean validateItem(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        if (member.getRole().equals(Role.ADMIN)){
            return true;
        }
        return false;
    }

    public void deleteItem(Long itemId){
        List<Member> members = memberRepository.findAll();
        for (Member mem : members){
            List<Long> itemIdlist = mem.getItemsId();
            if (itemIdlist != null){
                for (Long iId : itemIdlist){
                    if (iId.equals(itemId)){
                        itemIdlist.remove(itemId);
                        break;
                    }
                }
            }
        }
        memberRepository.saveAll(members);

        List<CartItem> cartItems = cartItemRepository.findByItemId(itemId);
        if (cartItems != null){
            for (CartItem cartItem : cartItems){
                cartItemRepository.delete(cartItem);
            }
        }
        Item item = itemRepository.findById(itemId).orElseThrow();
        DeleteItem deleteItem = new DeleteItem(item);
        deleteItemRepository.save(deleteItem);

        List<OrderItem> orderItems = orderItemRepository.findByItemId(itemId);
        if (orderItems != null){
            for (OrderItem orderItem : orderItems){
                Order order = orderItem.getOrder();
                if (order != null){
                    order.setOrderStatus(OrderStatus.RM);
                    orderRepository.save(order);
                }
                orderItem.setItem(null);
                orderItem.setDeleteItem(deleteItem);
                //orderItemRepository.delete(orderItem);
                orderItemRepository.save(orderItem);
            }
        }

        List<ItemImg> itemImg = itemImgRepository.findByItemId(itemId);
        for (ItemImg img : itemImg){
            DeleteItemImg deleteItemImg = new DeleteItemImg(img);
            deleteItemImg.setDeleteItem(deleteItem);
            deleteItemImgRepository.save(deleteItemImg);
            itemImgRepository.delete(img);
        }

        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemListDto> getItemListPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getItemListPage(itemSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public List<ItemListDto> getSelectItemList(SmallType smallType){

        return itemRepository.getSelectItemList(smallType);
    }

    @Transactional(readOnly = true)
    public List<ItemListDto> getBestItem(){
        return itemRepository.getBestItem();
    }

    @Transactional(readOnly = true)
    public List<ItemListDto> getPickItem(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        List<Long> itemIds = member.getItemsId();
        List<ItemListDto> items = new ArrayList<>();
        if (itemIds != null){
            for (Long itemId : itemIds){
                Item item = itemRepository.findById(itemId).orElseThrow();
                ItemImg itemimg = itemImgRepository.findByItemIdAndRepImgYn(item.getId(),"Y");
                ItemListDto itemListDto = new ItemListDto(item, itemimg);
                items.add(itemListDto);
            }
        }
        return items;
    }

    public List<ItemListDto> getBannerItem(){
        return itemRepository.getBannerItem();
    }




}
