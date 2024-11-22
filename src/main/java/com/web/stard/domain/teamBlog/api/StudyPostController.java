package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.teamBlog.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studies/{studyId}/study-posts")
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;


}
