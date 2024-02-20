package com.flower.controller;

import com.flower.dto.ItemListDto;
import com.flower.dto.ItemSearchDto;
import com.flower.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Model model) {
        int page = 0;
        int pageSize = 10;

        if (itemSearchDto.getSearchQuery() == null){
            itemSearchDto.setSearchQuery("");
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ItemListDto> items = itemService.getItemListPage(itemSearchDto, pageable);
        List<ItemListDto> bestItems = itemService.getBestItem();
        List<ItemListDto> bannerItems = itemService.getBannerItem();

        model.addAttribute("bannerItems", bannerItems);
        model.addAttribute("bestItems",bestItems);
        model.addAttribute("items", items);
        model.addAttribute("totalPage", items.getTotalPages());
        model.addAttribute("itemSearchDto", itemSearchDto);

        return "main";
    }

    @GetMapping(value = "/item/more")
    public @ResponseBody List<ItemListDto> loadMoreItems(ItemSearchDto itemSearchDto, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;

        if (itemSearchDto.getSearchQuery() == null) {
            itemSearchDto.setSearchQuery("");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ItemListDto> nextPageItems = itemService.getItemListPage(itemSearchDto, pageable);

        return nextPageItems.getContent();
    }

    @ModelAttribute("recentItems")
    public List<ItemListDto> getRecentItems(HttpSession httpSession){

        List<ItemListDto> recentItems = (List<ItemListDto>) httpSession.getAttribute("recentItems");
        if (recentItems == null){
            recentItems = new ArrayList<>();
        }
        return recentItems;
    }


}
