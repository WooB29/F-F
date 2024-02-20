package com.flower.dto;

import com.flower.entity.ItemImg;
import com.flower.entity.NoticeImg;
import com.flower.entity.QnaImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class BoardImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();


    public static BoardImgDto of(NoticeImg boardimg){
        return modelMapper.map(boardimg, BoardImgDto.class);
    }

    public static BoardImgDto of(QnaImg boardimg){
        return modelMapper.map(boardimg, BoardImgDto.class);
    }
}
