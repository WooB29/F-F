package com.flower.dto;

import com.flower.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {

    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();


    public static ItemImgDto of(ItemImg itemimg){
        return modelMapper.map(itemimg, ItemImgDto.class);
    }
}
