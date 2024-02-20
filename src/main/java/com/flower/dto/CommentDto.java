package com.flower.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flower.entity.Comment;
import com.flower.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;

    private Member member;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    //private LocalDateTime CreatedTime;

    private Comment parent;

    private List<Comment> children;

    private static ModelMapper modelMapper = new ModelMapper();

    public static CommentDto create(String content, Member member){
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(content);
        commentDto.setMember(member);
        return commentDto;
    }


    public static CommentDto toDto(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setMember(comment.getMember());
        commentDto.setParent(comment.getParent());
        commentDto.setChildren(comment.getChildren());
        return commentDto;
    }




}
