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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository toDoRepository;
    private final AssigneeRepository assigneeRepository;
    private final StudyService studyService;
    private final MemberRepository memberRepository;


    // 진행 중인 스터디인지 확인
    private void isStudyInProgress(Study study) {
        if (study.getProgressType() != ProgressType.IN_PROGRESS) {
            throw new CustomException(ErrorCode.STUDY_NOT_IN_PROGRESS);
        }
    }

    // TODO : 등록하는 회원이 스터디 멤버인지 확인
    private void isStudyMember(Study study, Member member) {

    }

    // 투두의 스터디랑 넘어온 스터디가 같은지 확인 (혹시 모를 오류 방지)
    private void isEqualToDoStudyAndStudy(Study study, ToDo toDo) {
        if (study.getId() != toDo.getStudy().getId()) {
            throw new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST);
        }
    }

    /**
     * 스터디 - 투두 등록
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param requestDto task 담당 업무, dueDate 마감일, assignees 담당자 (닉네임)
     *
     * @return ToDoDto toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public ToDoResponseDto.ToDoDto createToDo(Long studyId, ToDoRequestDto.CreateDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        isStudyInProgress(study);
        isStudyMember(study, member);

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

    /**
     * 스터디 - 투두 업무 내용 수정
     *
     * @param studyId 해당 study 고유 id
     * @param toDoId 해당 투두 고유 id
     * @param member 로그인 회원
     * @param requestDto task 담당 업무
     *
     * @return ToDoDto toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public ToDoResponseDto.ToDoDto updateTask(Long studyId, Long toDoId, ToDoRequestDto.TaskDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        isStudyInProgress(study);
        isStudyMember(study, member);

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST));

        isEqualToDoStudyAndStudy(study, toDo);

        toDo.updateTask(requestDto.getTask());

        return ToDoResponseDto.ToDoDto.from(toDo, toDo.getAssignees());
    }

    /**
     * 스터디 - 투두 마감일 수정
     *
     * @param studyId 해당 study 고유 id
     * @param toDoId 해당 투두 고유 id
     * @param member 로그인 회원
     * @param requestDto dueDate 마감일
     *
     * @return ToDoDto toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public ToDoResponseDto.ToDoDto updateDueDate(Long studyId, Long toDoId, ToDoRequestDto.DueDateDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        isStudyInProgress(study);
        isStudyMember(study, member);

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST));

        isEqualToDoStudyAndStudy(study, toDo);

        toDo.updateDueDate(requestDto.getDueDate());

        return ToDoResponseDto.ToDoDto.from(toDo, toDo.getAssignees());
    }

    /**
     * 스터디 - 투두 담당자 수정
     *
     * @param studyId 해당 study 고유 id
     * @param toDoId 해당 투두 고유 id
     * @param member 로그인 회원
     * @param requestDto assignees 담당자
     *
     * @return ToDoDto toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public ToDoResponseDto.ToDoDto updateAssignee(Long studyId, Long toDoId, ToDoRequestDto.AssigneeDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        isStudyInProgress(study);
        isStudyMember(study, member);

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST));

        isEqualToDoStudyAndStudy(study, toDo);

        // 기존 담당자와 비교 후 추가 및 삭제
        List<Assignee> assignees = new ArrayList<>(toDo.getAssignees());
        Iterator<Assignee> iterator = assignees.iterator();

        while (iterator.hasNext()) {
            Assignee assignee = iterator.next();

            if (!requestDto.getAssignees().stream()
                    .anyMatch(nickname -> nickname.equals(assignee.getMember().getNickname()))) {
                assignee.deleteAssignee(); // 관계 삭제
                toDo.getAssignees().remove(assignee); // 관계 삭제
                assigneeRepository.delete(assignee);
                iterator.remove();
            }
        }

        requestDto.getAssignees().forEach(nickname -> {
            if (!assignees.stream().anyMatch(assignee -> assignee.getMember().getNickname().equals(nickname))) {
                // TODO: Member -> StudyMember 변경
                Member assigneeMember = memberRepository.findByNickname(nickname)
                        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Assignee assigneeEntity = Assignee.builder()
                        .toDo(toDo)
                        .member(assigneeMember)
                        .toDoStatus(false)
                        .build();

                assigneeRepository.save(assigneeEntity);
                assignees.add(assigneeEntity);

                // 담당자가 새로 추가되었으므로 toDoStatus가 true일 시 false로 변경
                if (toDo.isToDoStatus()) {
                    toDo.updateToDoStatus(false);
                }
            }
        });

        return ToDoResponseDto.ToDoDto.from(toDo, assignees);
    }

    /**
     * 스터디 - 투두 상태 변화
     *
     * @param studyId 해당 study 고유 id
     * @param toDoId 해당 투두 고유 id
     * @param status 상태 (true - 완료, false - 미완료)
     * @param member 로그인 회원
     *
     * @return ToDoDto toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public ToDoResponseDto.ToDoDto updateTodoStatus(Long studyId, Long toDoId, Long assigneeId, boolean status, Member member) {
        Study study = studyService.findById(studyId);
        isStudyInProgress(study);
        isStudyMember(study, member);

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST));
        Assignee assignee = assigneeRepository.findById(assigneeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST));

        isEqualToDoStudyAndStudy(study, toDo);

        if (toDo.getId() != assignee.getToDo().getId()) { // 혹시 모를 오류 방지
            throw new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST);
        }

        // member == assignee의 member 확인 (본인 거 외에는 상태 변화 금지)
        if (assignee.getMember().getId() != member.getId()) {
            throw new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST);
        }

        if (assignee.isToDoStatus() != status) {
            assignee.updateToDoStatus(status);
        }

        // 투두 상태 변화 (모두 true일 시 true로 변경)
        if (toDo.getAssignees().stream().noneMatch(assigneeEntity -> !assigneeEntity.isToDoStatus())) {
            toDo.updateToDoStatus(true);
        } else {
            toDo.updateToDoStatus(false);
        }

        return ToDoResponseDto.ToDoDto.from(toDo, toDo.getAssignees());
    }

    /**
     * 스터디 - 투두 삭제
     *
     * @param studyId 해당 study 고유 id
     * @param toDoId  해당 투두 고유 id
     * @param member  로그인 회원
     */
    @Transactional
    @Override
    public Long deleteToDo(Long studyId, Long toDoId, Member member) {
        Study study = studyService.findById(studyId);
        isStudyInProgress(study);
        isStudyMember(study, member);

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_TODO_BAD_REQUEST));

        isEqualToDoStudyAndStudy(study, toDo);

        toDoRepository.delete(toDo);

        return toDoId;
    }

    /**
     * 스터디 - 전체 투두 조회 (월 단위)
     *
     * @param studyId 해당 study 고유 id
     * @param year 년도
     * @param month 월
     *
     * @return ToDoDto 리스트
     *          toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public List<ToDoResponseDto.ToDoDto> getAllToDoListByStudy(Long studyId, int year, int month) {
        Study study = studyService.findById(studyId);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());

        List<ToDo> toDoList = toDoRepository.findAllByStudyAndDueDateBetween(study, start, end);

        return toDoList.stream().map(toDo -> ToDoResponseDto.ToDoDto.from(toDo, toDo.getAssignees())).toList();
    }

    /**
     * 사용자 - 전체 투두 조회 (월 단위)
     *
     * @param member 로그인 회원
     * @param year 년도
     * @param month 월
     *
     * @return MemberToDoDto 리스트
     *          toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태, assignees 담당자 (닉네임, 투두 상태)
     */
    @Transactional
    @Override
    public List<ToDoResponseDto.MemberToDoDto> getMemberToDoList(Member member, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());

        // TODO: StudyMember로 변경
        List<Assignee> toDoList = assigneeRepository.findAllByMemberAndToDoDueDateBetween(member, start, end);

        return toDoList.stream().map(ToDoResponseDto.MemberToDoDto::of).toList();
    }

    /**
     * 사용자 - 스터디 별 투두 조회 (월 단위)
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param year 년도
     * @param month 월
     *
     * @return MemberToDoDto 리스트
     *          toDoId, task 담당 업무, dueDate 마감일, studyId, toDoStatus 투두 상태
     */
    @Transactional
    @Override
    public List<ToDoResponseDto.MemberToDoDto> getMemberToDoListByStudy(Long studyId, Member member, int year, int month) {
        Study study = studyService.findById(studyId);
        isStudyMember(study, member);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());

        // TODO: StudyMember로 변경
        List<Assignee> toDoList = assigneeRepository.findAllByMemberAndToDoStudyAndToDoDueDateBetween(member, study, start, end);

        return toDoList.stream().map(ToDoResponseDto.MemberToDoDto::of).toList();
    }
}