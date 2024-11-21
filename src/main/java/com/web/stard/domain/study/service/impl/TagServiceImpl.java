package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.study.repository.TagRepository;
import com.web.stard.domain.study.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;


}
