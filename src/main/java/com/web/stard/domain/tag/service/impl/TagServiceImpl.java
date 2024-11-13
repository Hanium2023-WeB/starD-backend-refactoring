package com.web.stard.domain.tag.service.impl;

import com.web.stard.domain.tag.repository.TagRepository;
import com.web.stard.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;


}
