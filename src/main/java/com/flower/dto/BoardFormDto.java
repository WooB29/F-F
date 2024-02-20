package com.flower.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flower.entity.Member;
import com.flower.entity.Notice;
import com.flower.entity.Qna;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardFormDto {
    private Long id;
    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Length(max = 70, message = "제목은 70자 이하로 해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime regTime;

    private int hits;

    private static ModelMapper modelMapper = new ModelMapper();

    private List<BoardImgDto> boardImgDtoList = new ArrayList<>();
    private List<Long> boardImgIds = new ArrayList<>();

    private Member member;

    public Notice createNotice(){
        return modelMapper.map(this, Notice.class);
    }

    public Qna createQna(){
        return modelMapper.map(this, Qna.class);
    }

    public static BoardFormDto of(Notice notice){
        return modelMapper.map(notice, BoardFormDto.class);
    }

    public static BoardFormDto of(Qna qna){
        return modelMapper.map(qna, BoardFormDto.class);
    }

    public BoardFormDto (Notice notice){
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.regTime = notice.getRegTime();
        this.member = notice.getMember();
        this.hits = notice.getHits();
    }

    public BoardFormDto (Qna qna){
        this.id = qna.getId();
        this.title = qna.getTitle();
        this.content = qna.getContent();
        this.regTime = qna.getRegTime();
        this.member = qna.getMember();
        this.hits = qna.getHits();
    }
}
