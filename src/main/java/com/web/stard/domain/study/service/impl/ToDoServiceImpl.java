package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.study.domain.dto.request.ToDoRequestDto;
import com.web.stard.domain.study.domain.dto.response.ToDoResponseDto;
import com.web.stard.domain.study.domain.entity.Assignee;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.ToDo;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.repository.AssigneeRepository;
import com.web.stard.domain.study.repository.ToDoRepository;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.study.service.ToDoService;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository toDoRepository;
    private final AssigneeRepository assigneeRepository;
    private final StudyService studyService;
    private final MemberRepository memberRepository;


    /**
     * 해당 게시글 공감 혹은 스크랩 삭제
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param requestDto task 담당 업무, dueDate 마감일, assignees 담당자 (닉네임)
     *
     * @return ToDoDto toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public ToDoResponseDto.ToDoDto createToDo(Long studyId, ToDoRequestDto.ToDoCreateDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        // 진행 중인 스터디인지 확인
        if (study.getProgressType() != ProgressType.IN_PROGRESS) {
            throw new CustomException(ErrorCode.STUDY_NOT_IN_PROGRESS);
        }

        // TODO: 등록하는 회원이 해당 스터디 멤버인지 확인


        // 투두 저장
        ToDo toDo = ToDo.builder()
                .task(requestDto.getTask())
                .dueDate(requestDto.getDueDate())
                .study(study)
                .toDoStatus(false)
                .build();

        toDoRepository.save(toDo);

        // 담당자 저장
        List<Assignee> assignees = requestDto.getAssignees().stream().map(nickname -> {
            // TODO: Member -> StudyMember 변경
            Member assignee = memberRepository.findByNickname(nickname)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            return Assignee.builder()
                    .toDo(toDo)
                    .member(assignee)
                    .toDoStatus(false)
                    .build();
        }).toList();

        assigneeRepository.saveAll(assignees);

        return ToDoResponseDto.ToDoDto.from(toDo, assignees);
    }
}
