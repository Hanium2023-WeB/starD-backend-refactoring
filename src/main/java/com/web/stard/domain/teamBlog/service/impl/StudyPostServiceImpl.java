package com.web.stard.domain.teamBlog.service.impl;

import com.web.stard.domain.teamBlog.repository.StudyPostRepository;
import com.web.stard.domain.teamBlog.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyPostServiceImpl implements StudyPostService {

    private final StudyPostRepository studyPostRepository;


}
