package com.flower.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunitySearchDto {

    private String searchBy;
    private String searchQuery = "";
}
