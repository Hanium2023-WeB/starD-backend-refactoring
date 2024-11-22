package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.study.domain.dto.request.StudyRequestDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.domain.study.service.StudyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudyServiceImplTest {


    @Autowired
    private StudyService studyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;



}