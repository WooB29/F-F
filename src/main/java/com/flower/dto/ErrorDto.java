package com.flower.dto;

import com.flower.constant.ErrorStatus;
import com.flower.entity.Error;
import com.flower.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;


@Getter
@Setter
public class ErrorDto {
    private Long errorId;
    private String errorDate;
    private ErrorStatus errorStatus;
    private Long memberId;
    private int code;
    private String location;
    private String comment;
    private String content;

    public ErrorDto(Error error, Member member) {
        this.errorId = error.getId();
        this.errorDate = error.getRegTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.errorStatus = error.getErrorStatus();
        this.memberId = member.getId();
        this.code = error.getCode();
        this.location = error.getLocation();
        this.comment = error.getComment();
        this.content = error.getContent();
    }
}
