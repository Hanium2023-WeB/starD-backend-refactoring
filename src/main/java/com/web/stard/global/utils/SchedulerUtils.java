package com.web.stard.global.utils;

import com.web.stard.domain.study.domain.enums.RecruitmentType;
import com.web.stard.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerUtils {

    private final StudyRepository studyRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateRecruitmentStatusToCompleted() {
        log.info("Updating recruitment status for studies with past deadlines started");
        studyRepository.findByRecruitmentDeadlineBeforeAndRecruitmentType(LocalDate.now(), RecruitmentType.RECRUITING)
                .forEach(study -> study.updateRecruitmentType(RecruitmentType.COMPLETED));
        log.info("Updating recruitment status for studies with past deadlines completed");
    }

}
