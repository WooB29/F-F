package com.flower.dto;

import com.flower.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberEditDto {

    private String email;
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


    public MemberEditDto(Member member){
        this.email = member.getEmail();
        this.name = member.getName();
        this.phone = member.getPhone();
        if (member.getWay().equals("회원가입")){
            String[] address = member.getAddress().split("/");
            this.address1 = address[0];
            this.address2 = address[1];
            this.address3 = address[2];
        }
        else {
            this.address1 = "";
            this.address2 = "";
            this.address3 = "";
        }
    }


}
