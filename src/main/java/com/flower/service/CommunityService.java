package com.flower.service;

import com.flower.dto.*;
import com.flower.entity.*;
import com.flower.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final NoticeRepository noticeRepository;
    private final CommunityImgService communityImgService;
    private final NoticeImgRepository noticeImgRepository;
    private final QnaRepository qnaRepository;
    private final QnaImgRepository qnaImgRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<BoardFormDto> getNoticePage(CommunitySearchDto communitySearchDto, Pageable pageable){
        Page<Notice> notices =noticeRepository.getNoticePage(communitySearchDto,pageable);
        return notices.map(BoardFormDto::new);
    }

    public Long saveNotice(BoardFormDto boardFormDto, List<MultipartFile> boardImgFileList) throws Exception{
        Notice notice = boardFormDto.createNotice();
        noticeRepository.save(notice);

        for (int i =0; i<boardImgFileList.size(); i++){
            NoticeImg noticeImg = new NoticeImg();
            noticeImg.setNotice(notice);
            if (i==0) {
                noticeImg.setRepImgYn("Y");
            }
            else {
                noticeImg.setRepImgYn("N");
                communityImgService.saveNoticeImg(noticeImg, boardImgFileList.get(i));
            }
        }
        return notice.getId();
    }

    @Transactional
    public void updateHits(Long id){
        noticeRepository.updateHits(id);
    }
    @Transactional
    public void updateHitsQna(Long id){
        qnaRepository.updateHits(id);
    }

    @Transactional(readOnly = true)
    public BoardFormDto getNoticeDtl(Long noticeId){
        List<NoticeImg> noticeImgList = noticeImgRepository.findByNoticeIdOrderByIdAsc(noticeId);
        List<BoardImgDto> boardImgDtoList = new ArrayList<>();

        for (NoticeImg noticeImg : noticeImgList) {
            BoardImgDto boardImgDto = BoardImgDto.of(noticeImg);

            boardImgDtoList.add(boardImgDto);
        }
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new);
        BoardFormDto boardFormDto = BoardFormDto.of(notice);
        boardFormDto.setBoardImgDtoList(boardImgDtoList);
        return boardFormDto;
    }

    public Long updateNotice(BoardFormDto boardFormDto, List<MultipartFile> noticeImgFileList) throws Exception{
        Notice notice = noticeRepository.findById(boardFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        notice.updateNotice(boardFormDto);

        List<Long> noticeImgIds = boardFormDto.getBoardImgIds();

        for (int i =0; i<noticeImgFileList.size(); i++){
            communityImgService.updateNoticeImg(noticeImgIds.get(i), noticeImgFileList.get(i));
        }
        return notice.getId();
    }

    public boolean deleteNotice(Long noticeId, String email)  {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow();
        Member member = memberRepository.findByEmail(email).orElseThrow();
        if (!(notice.getMember().equals(member))){
            return false;
        }
        List<NoticeImg> noticeImg = noticeImgRepository.findByNoticeId(noticeId);
        for (int i =0; i<noticeImg.size(); i++){
            noticeImgRepository.delete(noticeImg.get(i));
        }
        noticeRepository.delete(notice);
        return true;
    }

    @Transactional(readOnly = true)
    public Page<BoardFormDto> getQnaPage(CommunitySearchDto communitySearchDto, Pageable pageable){
        Page<Qna> qnas = qnaRepository.getQnaPage(communitySearchDto,pageable);
        return qnas.map(BoardFormDto::new);

    }


    public Long saveQna(BoardFormDto boardFormDto, List<MultipartFile> boardImgFileList) throws Exception{
        Qna qna = boardFormDto.createQna();
        qnaRepository.save(qna);
        for (int i =0; i<boardImgFileList.size(); i++){
            QnaImg qnaImg = new QnaImg();
            qnaImg.setQna(qna);
            if (i==0) {
                qnaImg.setRepImgYn("Y");
            }
            else {
                qnaImg.setRepImgYn("N");
                communityImgService.saveQnaImg(qnaImg, boardImgFileList.get(i));
            }
        }
        return qna.getId();
    }

    @Transactional(readOnly = true)
    public BoardFormDto getQnaDtl(Long qnaId){
        List<QnaImg> qnaImgList = qnaImgRepository.findByQnaIdOrderByIdAsc(qnaId);
        List<BoardImgDto> boardImgDtoList = new ArrayList<>();

        for (QnaImg qnaImg : qnaImgList) {
            BoardImgDto boardImgDto = BoardImgDto.of(qnaImg);

            boardImgDtoList.add(boardImgDto);
        }
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(EntityNotFoundException::new);
        BoardFormDto boardFormDto = BoardFormDto.of(qna);
        boardFormDto.setBoardImgDtoList(boardImgDtoList);
        return boardFormDto;
    }

    public Long updateQna(BoardFormDto boardFormDto, List<MultipartFile> noticeImgFileList) throws Exception{
        Qna qna = qnaRepository.findById(boardFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        qna.updateQna(boardFormDto);

        List<Long> qnaImgIds = boardFormDto.getBoardImgIds();


        for (int i =0; i<noticeImgFileList.size(); i++){

            communityImgService.updateNoticeImg(qnaImgIds.get(i), noticeImgFileList.get(i));
        }
        return qna.getId();
    }

    public boolean deleteQna(Long qnaId, String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(EntityNotFoundException::new);
        if (!(qna.getMember().getId().equals(member.getId()))){
            return false;
        }
        List<QnaImg> qnaImg = qnaImgRepository.findByQnaId(qnaId);
        for (int i =0; i<qnaImg.size(); i++){
            qnaImgRepository.delete(qnaImg.get(i));
        }
        qnaRepository.delete(qna);
        return true;
    }

    @Transactional
    public List newComment(Member member, Map<String, Object> data){
        String content = (String) data.get("content");
        CommentDto commentDto = CommentDto.create(content, member);
        Comment comment = Comment.createComment(commentDto);
        commentRepository.save(comment);

        String where = (String) data.get("where");
        Long boardId = ((Integer) data.get("boardId")).longValue();

        if (where.equals("qna")){
            Qna qna = qnaRepository.findById(boardId).orElseThrow();
            qna.addComment(comment);
            qnaRepository.save(qna);
        }
        else {
            Notice notice = noticeRepository.findById(boardId).orElseThrow();
            notice.addComment(comment);
            noticeRepository.save(notice);
        }
        CommentDto commentDto1 = CommentDto.toDto(comment);
        List<CommentDto> commentDtoList = new ArrayList<>();
        commentDtoList.add(commentDto1);



        return commentDtoList;
    }

    @Transactional(readOnly = true)
    public List<Comment> getNoticeCommentList(Long noticeId){
        return noticeRepository.getNoticeCommentList(noticeId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getQnaCommentList(Long qnaId){
        return qnaRepository.getQnaCommentList(qnaId);
    }

    public void commentDelete(Map<String, Object> data){
        String where = (String) data.get("where");
        Long commentId = ((Integer) data.get("commentId")).longValue();
        Long boardId = ((Integer) data.get("boardId")).longValue();
        if (where.equals("qna")){
            Qna qna = qnaRepository.findById(boardId).orElseThrow();
            List<Comment> commentList = qna.getComments();
            for (Comment comment : commentList){
                if (comment.getId().equals(commentId)){
                    commentList.remove(comment);
                    break;
                }
            }
            qnaRepository.save(qna);
        }
        else {
            Notice notice = noticeRepository.findById(boardId).orElseThrow();
            List<Comment> commentList = notice.getComments();
            for (Comment comment : commentList){
                if (comment.getId().equals(commentId)){
                    commentList.remove(comment);
                    break;
                }
            }
            noticeRepository.save(notice);
        }
    }

    public Long commentModify(Map<String, Object> data){
        String content = (String) data.get("content");
        Long commentId = ((Integer) data.get("commentId")).longValue();

        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setContent(content);
        commentRepository.save(comment);
        return comment.getId();
    }

    @Transactional
    public Long addComment(Member member, Map<String, Object> data){
        String content = (String) data.get("addContent");
        Long commentId = ((Integer) data.get("commentId")).longValue();
        CommentDto commentDto = CommentDto.create(content, member);
        Comment child = Comment.createComment(commentDto);
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        child.setParent(comment);

        commentRepository.save(child);


        return commentId;
    }

    public void commentChildDelete(Map<String, Object> data){
        Long commentId = ((Integer) data.get("commentId")).longValue();
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        commentRepository.delete(comment);
    }

    public Long commentChildModify(Map<String, Object> data) {
        String content = (String) data.get("childContent");
        Long commentId = ((Integer) data.get("commentId")).longValue();

        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setContent(content);
        commentRepository.save(comment);
        return comment.getId();
    }


}
