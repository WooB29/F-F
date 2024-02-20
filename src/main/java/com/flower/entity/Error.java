package com.flower.entity;

import com.flower.constant.ErrorStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "error")
@NoArgsConstructor
public class Error extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "error_id")
    private Long id;
    @Column
    private String email;
    @Column
    private int code;
    @Column
    private String content;
    @Column
    private String location;
    @Column
    private String comment;
    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;

    public Error(String email, int code, String content, String location,String comment, ErrorStatus errorStatus) {
        this.email = email;
        this.code = code;
        this.content = content;
        this.location = location;
        this.comment = comment;
        this.errorStatus = errorStatus;
    }
}
