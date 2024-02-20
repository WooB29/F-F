package com.flower.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "deleteItem_img")
@Getter
@Setter
public class DeleteItemImg extends BaseEntity{
    @Id
    @Column(name = "deleteItem_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleteItem_id")
    private DeleteItem deleteItem;

    public DeleteItemImg(){};

    public DeleteItemImg(ItemImg itemImg){
        this.imgName = itemImg.getImgName();
        this.oriImgName = itemImg.getOriImgName();
        this.imgUrl = itemImg.getImgUrl();
        this.repImgYn = itemImg.getRepImgYn();
    }
}
