package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.study.repository.StudyPostRepository;
import com.web.stard.domain.study.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyPostServiceImpl implements StudyPostService {

    private final StudyPostRepository studyPostRepository;


}
