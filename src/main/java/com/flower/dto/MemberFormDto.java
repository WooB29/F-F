package com.flower.dto;

import com.flower.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
public class MemberFormDto {
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4, max = 8, message = "비밀번호는 4자 이상, 8자 이하로 입력해주세요.")
    private String password;
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @NotEmpty(message = "전화번호는 필수 입력 값입니다.")
    private String phone;
    @NotEmpty(message = "우편번호는 필수 입력 값입니다.")
    private String address1;
    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address2;
    @NotEmpty(message = "상세주소는 필수 입력 값입니다.")
    private String address3;
    private int check;


    public MemberFormDto(){}

    public MemberFormDto(Member member){
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.phone = member.getPhone();
        String[] address = member.getAddress().split("/");
        this.address1 = address[0];
        this.address2 = address[1];
        this.address3 = address[2];
    }
}
