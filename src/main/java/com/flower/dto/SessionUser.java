package com.flower.dto;

import com.flower.constant.Role;
import com.flower.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private Role role;
    private String way;
    private String displayName;

    public SessionUser(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.role = member.getRole();
        this.way = member.getWay();
        this.displayName = member.getName();
    }
}
