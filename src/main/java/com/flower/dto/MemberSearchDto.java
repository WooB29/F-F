package com.flower.dto;

import com.flower.constant.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchDto {


    private String searchDateType;

    private String searchWay;

    private Role searchRole;

    private String searchName;

    private String searchQuery = "";


}
