package com.web.stard.global.utils;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.member.service.MemberService;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import com.web.stard.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerUtils {

    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateRecruitmentStatusToCompleted() {
        log.info("Updating recruitment status for studies with past deadlines started");
        studyRepository.findByRecruitmentDeadlineBeforeAndRecruitmentType(LocalDate.now(), RecruitmentType.RECRUITING)
                .forEach(study -> study.updateRecruitmentType(RecruitmentType.COMPLETED));
        log.info("Updating recruitment status for studies with past deadlines completed");
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateProgressStatusToCompleted() {
        log.info("Updating progress status for studies with past deadline started");
        studyRepository.findByActivityDeadlineBeforeAndProgressType(LocalDate.now(), ProgressType.IN_PROGRESS)
                .forEach(study -> study.updateProcessType(ProgressType.COMPLETED));
        log.info("Updating progress status for studies with past deadline completed");
    }

    /**
     * 누적 신고 수가 5회 이상인 회원 강제 탈퇴
     */
    @Scheduled(cron = "0 30 0 * * *")
    @Transactional
    public void forceDeleteMembersWithExcessReports() {
        log.info("Force delete members with excess reports started");

        List<Member> membersToDelete = memberRepository.findByReportCountGreaterThanEqual(5);

        for (Member member : membersToDelete) {
            try {
                memberService.deleteAllRelatedEntities(member, true);
                memberRepository.delete(member);
            } catch (Exception e) {
                log.info("Force deletion failed for member: " + member.getId() + " - " + e.getMessage());
            }
        }

        log.info("Force delete members with excess reports completed");
    }

}
