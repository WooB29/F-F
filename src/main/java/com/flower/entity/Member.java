package com.flower.entity;

import com.flower.constant.Role;
import com.flower.dto.MemberEditDto;
import com.flower.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="member")
@Getter
@Setter
@NoArgsConstructor
public class Member  extends BaseEntity{
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String name;
    private String address;
    private String phone;
    private String way;
    @Enumerated(EnumType.STRING)
    private Role role;

    private List<Long> itemsId = new ArrayList<>();

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();

        member.setEmail(memberFormDto.getEmail());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setName(memberFormDto.getName());
        member.setPhone(memberFormDto.getPhone());
        member.setAddress(memberFormDto.getAddress1()+"/"+memberFormDto.getAddress2()+"/"+memberFormDto.getAddress3());
        member.setRole(Role.USER);
        member.setWay("회원가입");
        return member;
    }
    public Member(String name, String email, Role role, String way){
        this.name=name;
        this.email=email;
        this.role=role;
        this.way=way;
    }
    public Member update(String name, String way){
        this.name = name;
        this.way = way;
        return this;
    }

    public void modify(String name, Role role){
        this.name = name;
        this.role = role;
    }

    public void memberModify(MemberEditDto memberEditDto){
        setName(memberEditDto.getName());
        setPhone(memberEditDto.getPhone());
        setAddress(memberEditDto.getAddress1()+"/"+memberEditDto.getAddress2()+"/"+memberEditDto.getAddress3());
    }

}
