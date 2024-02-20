package com.flower.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TodaySearchDto {
    private String month;
    private String day;
    private String searchType;
    private String searchWord;
    private String pageNo;
}
