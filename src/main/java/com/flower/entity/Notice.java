package com.flower.entity;

import com.flower.constant.Role;
import com.flower.dto.BoardFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="notice")
@Getter
@Setter
@ToString
public class Notice extends BaseEntity{
    @Id
    @Column(name="notice_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int hits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();


    public void updateNotice(BoardFormDto boardFormDto){
        this.id = boardFormDto.getId();
        this.title = boardFormDto.getTitle();
        this.content = boardFormDto.getContent();
        this.member = boardFormDto.getMember();
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }
}