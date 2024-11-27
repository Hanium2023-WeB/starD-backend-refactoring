package com.web.stard.domain.teamBlog.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.reply.repository.ReplyRepository;
import com.web.stard.domain.starScrap.domain.enums.ActType;
import com.web.stard.domain.starScrap.domain.enums.TableType;
import com.web.stard.domain.starScrap.service.StarScrapService;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.teamBlog.domain.dto.response.StudyPostResponseDto;
import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import com.web.stard.domain.teamBlog.domain.entity.StudyPostFile;
import com.web.stard.domain.teamBlog.repository.StudyPostFileRepository;
import com.web.stard.domain.teamBlog.repository.StudyPostRepository;
import com.web.stard.domain.teamBlog.service.StudyPostService;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyPostServiceImpl implements StudyPostService {

    private final StudyPostRepository studyPostRepository;
    private final StudyService studyService;
    private final StudyMemberRepository studyMemberRepository;
    private final S3Manager s3Manager;
    private final StudyPostFileRepository studyPostFileRepository;
    private final StarScrapService starScrapService;
    private final ReplyRepository replyRepository;


    // id로 StudyPost 찾기
    private StudyPost findStudyPost(Long id) {
        return studyPostRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_POST_NOT_FOUND));
    }

    // 스터디게시글의 스터디랑 넘어온 스터디가 같은지 확인 (혹시 모를 오류 방지)
    private void isEqualStudyPostStudyAndStudy(Study study, StudyPost studyPost) {
        if (study.getId() != studyPost.getStudy().getId()) {
            throw new CustomException(ErrorCode.STUDY_POST_BAD_REQUEST);
        }
    }

    // 작성자인지 확인
    private boolean isPostAuthor(StudyMember studyMember, StudyPost studyPost) {
        if (studyMember.getMember().getId() != studyPost.getStudyMember().getMember().getId()) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        return true;
    }


    /**
     * 스터디 - 커뮤니티 게시글 등록
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param files 다중 파일
     * @param requestDto title 제목, content 내용
     *
     * @return StudyPostDto
     *      studyPostId, studyId, writer 작성자, profileImg 프로필 이미지, title 제목, content 내용, files 파일경로들, isAuthor 작성자 여부
     */
    @Transactional
    @Override
    public StudyPostResponseDto.StudyPostDto createStudyPost(Long studyId, List<MultipartFile> files, PostRequestDto.CreatePostDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);
        studyService.isStudyMember(study, member);

        StudyMember studyMember = studyMemberRepository.findByStudyAndMember(study, member)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));

        StudyPost studyPost = StudyPost.builder()
                .study(study)
                .studyMember(studyMember)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .postType(PostType.STUDYPOST)
                .hit(0)
                .build();

        studyPost = studyPostRepository.save(studyPost);

        // 다중 파일 저장
        if (files != null && !files.isEmpty()) {
            List<String> keyNames = new ArrayList<>();
            List<String> fileUrls;

            for (MultipartFile file : files) {
                UUID uuid = UUID.randomUUID();
                keyNames.add(s3Manager.generateStudyPostKeyName(uuid));
            }

            fileUrls = s3Manager.uploadFiles(keyNames, files);

            for (int i = 0; i < fileUrls.size(); i++) {
                MultipartFile file = files.get(i);
                String originalFileName = file.getOriginalFilename();

                StudyPostFile studyPostFile = StudyPostFile.builder()
                        .studyPost(studyPost)
                        .fileName(originalFileName != null ? originalFileName : keyNames.get(i))
                        .fileUrl(fileUrls.get(i))
                        .build();

                studyPost.addFile(studyPostFile);
            }
        }

        studyPostRepository.save(studyPost);

        return StudyPostResponseDto.StudyPostDto.from(studyPost, 0, true, false);
    }

    /**
     * 스터디 - 커뮤니티 게시글 수정
     *
     * @param studyId 해당 study 고유 id
     * @param studyPostId  해당 게시글 고유 id
     * @param member 로그인 회원
     * @param files 새로 추가할 다중 파일
     * @param requestDto title 제목, content 내용, deleteFileId 삭제할 StudyPostFileId
     *
     * @return StudyPostDto
     *      studyPostId, studyId, writer 작성자, profileImg 프로필 이미지, title 제목, content 내용, files 파일경로들, isAuthor 작성자 여부
     */
    @Transactional
    @Override
    public StudyPostResponseDto.StudyPostDto updateStudyPost(Long studyId, Long studyPostId, List<MultipartFile> files, PostRequestDto.UpdateStudyPostDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);
        studyService.isStudyMember(study, member);

        StudyPost studyPost = findStudyPost(studyPostId);
        isEqualStudyPostStudyAndStudy(study, studyPost);
        StudyMember studyMember = studyMemberRepository.findByStudyAndMember(study, member)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));
        boolean isAuthor = isPostAuthor(studyMember, studyPost);

        // 파일 최대, 최소 개수 확인
        int originFileCount = (studyPost.getFiles() != null) ? studyPost.getFiles().size() : 0;
        int deleteCount = (requestDto.getDeleteFileId() != null) ? requestDto.getDeleteFileId().size() : 0;
        int addCount = (files != null) ? files.size() : 0;
        if (originFileCount - deleteCount + addCount > 5) {
            throw new CustomException(ErrorCode.STUDY_POST_MAX_FILES_ALLOWED);
        } else if (originFileCount - deleteCount + addCount < 0) {
            throw new CustomException(ErrorCode.STUDY_POST_BAD_REQUEST);
        }

        // 내용 수정
        studyPost.updateStudyPost(requestDto.getTitle(), requestDto.getContent());

        // 파일 삭제
        if (requestDto.getDeleteFileId() != null && !requestDto.getDeleteFileId().isEmpty()) {
            if (studyPost.getFiles() == null) {
                throw new CustomException(ErrorCode.STUDY_POST_FILE_NOT_FOUND);
            }

            List<String> deleteFileUrls = new ArrayList<>();
            studyPost.getFiles().removeIf(studyPostFile -> {
                if (requestDto.getDeleteFileId().contains(studyPostFile.getId())) {
                    deleteFileUrls.add(studyPostFile.getFileUrl()); // url 수집

                    studyPostFile.deleteStudyPost(); // 관계 제거
                    return true;
                }
                return false;
            }); // DB 삭제

            s3Manager.deleteFiles(deleteFileUrls); // s3 삭제
        }

        // 파일 추가
        if (files != null && !files.isEmpty()) {
            List<String> keyNames = new ArrayList<>();
            List<String> fileUrls;

            for (MultipartFile file : files) {
                UUID uuid = UUID.randomUUID();
                keyNames.add(s3Manager.generateStudyPostKeyName(uuid));
            }

            fileUrls = s3Manager.uploadFiles(keyNames, files);

            for (int i = 0; i < fileUrls.size(); i++) {
                MultipartFile file = files.get(i);
                String originalFileName = file.getOriginalFilename();

                StudyPostFile studyPostFile = StudyPostFile.builder()
                        .studyPost(studyPost)
                        .fileName(originalFileName != null ? originalFileName : keyNames.get(i))
                        .fileUrl(fileUrls.get(i))
                        .build();

                studyPost.getFiles().add(studyPostFile);
            }
        }

        studyPostRepository.save(studyPost);
        int scrapCount = starScrapService.findStarScrapCount(studyPost.getId(), ActType.SCRAP, TableType.STUDYPOST);
        boolean existsScrap = (starScrapService.existsStarScrap(member, studyPost.getId(), ActType.SCRAP, TableType.STUDYPOST) != null);

        return StudyPostResponseDto.StudyPostDto.from(studyPost, scrapCount, isAuthor, existsScrap);
    }

    /**
     * 스터디 - 커뮤니티 게시글 삭제
     *
     * @param studyId 해당 study 고유 id
     * @param studyPostId  해당 게시글 고유 id
     * @param member  로그인 회원
     */
    @Transactional
    @Override
    public Long deleteStudyPost(Long studyId, Long studyPostId, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);
        studyService.isStudyMember(study, member);

        StudyPost studyPost = findStudyPost(studyPostId);
        isEqualStudyPostStudyAndStudy(study, studyPost);
        StudyMember studyMember = studyMemberRepository.findByStudyAndMember(study, member)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));
        isPostAuthor(studyMember, studyPost);

        // 파일 삭제
        if (studyPost.getFiles() != null) {
            List<String> fileUrls = studyPost.getFiles().stream().map(StudyPostFile::getFileUrl).toList();
            s3Manager.deleteFiles(fileUrls);
        }

        // 스크랩 삭제
        starScrapService.deletePostStarScraps(studyPostId, ActType.SCRAP, TableType.STUDYPOST);

        // TODO 댓글 삭제



        studyPostRepository.delete(studyPost);

        return studyPostId;
    }

    /**
     * 스터디 - 커뮤니티 게시글 상세조회
     *
     * @param studyId 해당 study 고유 id
     * @param studyPostId  해당 게시글 고유 id
     * @param member 로그인 회원
     *
     * @return StudyPostDto
     *      studyPostId, studyId, writer 작성자, profileImg 프로필 이미지, title 제목, content 내용, files 파일경로들, isAuthor 작성자 여부
     */
    @Transactional
    @Override
    public StudyPostResponseDto.StudyPostDto getStudyPostDetail(Long studyId, Long studyPostId, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyMember(study, member);

        StudyPost studyPost = findStudyPost(studyPostId);
        isEqualStudyPostStudyAndStudy(study, studyPost);

        boolean isAuthor = (studyPost.getStudyMember().getMember().getId() == member.getId());

        if (!isAuthor) {
            studyPost.incrementHitCount();
        }

        int scrapCount = starScrapService.findStarScrapCount(studyPost.getId(), ActType.SCRAP, TableType.STUDYPOST);
        boolean existsScrap = (starScrapService.existsStarScrap(member, studyPost.getId(), ActType.SCRAP, TableType.STUDYPOST) != null);

        return StudyPostResponseDto.StudyPostDto.from(studyPost, scrapCount, isAuthor, existsScrap);
    }

    // 목록 조회 - 스크랩 수, 스크랩 여부 추가 메서드
    private List<StudyPostResponseDto.StudyPostItem> findAllScrap(Page<StudyPost> studyPosts, Member member) {
        return studyPosts.getContent().stream().map(studyPost -> {
            int scrapCount = starScrapService.findStarScrapCount(studyPost.getId(), ActType.SCRAP, TableType.STUDYPOST);
            boolean existsScrap = (starScrapService.existsStarScrap(member, studyPost.getId(), ActType.SCRAP, TableType.STUDYPOST) != null);

            return StudyPostResponseDto.StudyPostItem.of(studyPost, scrapCount, existsScrap);
        }).toList();
    }


    /**
     * 스터디 - 커뮤니티 게시글 전체 조회
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param page 조회할 페이지 번호
     *
     * @return StudyPostListDto
     *      studyId, StudyPostItem, currentPage 현재 페이지, totalPages 전체 페이지 수, isLast 마지막 페이지 여부
     *      StudyPostItem : studyPostId, writer 작성자, profileImg 프로필 이미지, title 제목, hit 조회수, scrapCount 스크랩 개수, totalFiles 파일 수, existsScrap 스크랩 여부
     */
    @Transactional(readOnly = true)
    @Override
    public StudyPostResponseDto.StudyPostListDto getStudyPostList(Long studyId, Member member, int page) {
        Study study = studyService.findById(studyId);
        studyService.isStudyMember(study, member);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<StudyPost> studyPosts = studyPostRepository.findByStudy(study, pageable);

        List<StudyPostResponseDto.StudyPostItem> studyPostItems = findAllScrap(studyPosts, member);

        return StudyPostResponseDto.StudyPostListDto.of(studyId, studyPosts, studyPostItems);
    }

    /**
     * 스터디 - 커뮤니티 게시글 키워드 검색
     *
     * @param studyId 해당 study 고유 id
     * @param keyword 조회할 키워드
     * @param member 로그인 회원
     * @param page 조회할 페이지 번호
     *
     * @return StudyPostListDto
     *      studyId, StudyPostItem, currentPage 현재 페이지, totalPages 전체 페이지 수, isLast 마지막 페이지 여부
     *      StudyPostItem : studyPostId, writer 작성자, profileImg 프로필 이미지, title 제목, hit 조회수, scrapCount 스크랩 개수, totalFiles 파일 수, existsScrap 스크랩 여부
     */
    @Transactional(readOnly = true)
    @Override
    public StudyPostResponseDto.StudyPostListDto searchStudyPost(Long studyId, String keyword, Member member, int page) {
        Study study = studyService.findById(studyId);
        studyService.isStudyMember(study, member);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<StudyPost> studyPosts = studyPostRepository.findByStudyAndTitleContainingOrContentContaining(study, keyword, keyword, pageable);

        List<StudyPostResponseDto.StudyPostItem> studyPostItems = findAllScrap(studyPosts, member);

        return StudyPostResponseDto.StudyPostListDto.of(studyId, studyPosts, studyPostItems);
    }

    /**
     * 사용자 - 스터디 팀블로그 커뮤니티 작성한 게시글 스터디별 조회
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param page 조회할 페이지 번호
     *
     * @return StudyPostListDto
     *      studyId, StudyPostItem, currentPage 현재 페이지, totalPages 전체 페이지 수, isLast 마지막 페이지 여부
     *      StudyPostItem : studyPostId, writer 작성자, profileImg 프로필 이미지, title 제목, hit 조회수, scrapCount 스크랩 개수, totalFiles 파일 수, existsScrap 스크랩 여부
     */
    @Transactional(readOnly = true)
    @Override
    public StudyPostResponseDto.StudyPostListDto getMemberStudyPostListByStudy(Long studyId, Member member, int page) {
        Study study = studyService.findById(studyId);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<StudyPost> studyPosts = studyPostRepository.findByStudyMember_MemberAndStudy(member, study, pageable);

        List<StudyPostResponseDto.StudyPostItem> studyPostItems = findAllScrap(studyPosts, member);

        return StudyPostResponseDto.StudyPostListDto.of(studyId, studyPosts, studyPostItems);
    }
}
