package com.flower.service;

import com.flower.entity.NoticeImg;
import com.flower.entity.QnaImg;
import com.flower.repository.NoticeImgRepository;
import com.flower.repository.QnaImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityImgService {
    @Value("${itemImgLocation}")
    private String communityImgLocation;

    private final FileService fileService;
    private final NoticeImgRepository noticeImgRepository;
    private final QnaImgRepository qnaImgRepository;

    public void saveNoticeImg(NoticeImg noticeImg, MultipartFile noticeImgFile) throws Exception{
        String oriImgName = noticeImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";
        if (!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(communityImgLocation, oriImgName, noticeImgFile.getBytes());
            imgUrl = "/images/item/"+imgName;
        }
        noticeImg.updateNoticeImg(oriImgName, imgName, imgUrl);
        noticeImgRepository.save(noticeImg);
    }

    public void saveQnaImg(QnaImg qnaImg, MultipartFile qnaImgFile) throws Exception{
        String oriImgName = qnaImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";
        if (!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(communityImgLocation, oriImgName, qnaImgFile.getBytes());
            imgUrl = "/images/item/"+imgName;
        }
        qnaImg.updateQnaImg(oriImgName, imgName, imgUrl);
        qnaImgRepository.save(qnaImg);
    }

    public void updateNoticeImg(Long noticeImgId, MultipartFile noticeImgFile) throws Exception{
        if (!noticeImgFile.isEmpty()){
            NoticeImg savedNoticeImg = noticeImgRepository.findById(noticeImgId).
                    orElseThrow(EntityNotFoundException::new);
            if (!StringUtils.isEmpty(savedNoticeImg.getImgName())){
                fileService.deleteFile(communityImgLocation+"/"+savedNoticeImg.getImgName());
            }
            String oriImgName = noticeImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(communityImgLocation, oriImgName,
                    noticeImgFile.getBytes());
            String imgUrl = "/images/item/"+imgName;
            savedNoticeImg.updateNoticeImg(oriImgName, imgName, imgUrl);
        }
    }

    public void updateQnaImg(Long qnaImgId, MultipartFile qnaImgFile) throws Exception{
        if (!qnaImgFile.isEmpty()){
            QnaImg saveQnaImg = qnaImgRepository.findById(qnaImgId).orElseThrow(EntityNotFoundException::new);

            if (!StringUtils.isEmpty(saveQnaImg.getImgName())){
                fileService.deleteFile(communityImgLocation+"/"+saveQnaImg.getImgName());
            }
            String oriImgName = qnaImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(communityImgLocation, oriImgName,
                    qnaImgFile.getBytes());
            String imgUrl = "/images/item/"+imgName;
            saveQnaImg.updateQnaImg(oriImgName, imgName, imgUrl);
        }
    }
}
