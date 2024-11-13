package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyTag;
import com.web.stard.domain.study.repository.StudyTagRepository;
import com.web.stard.domain.study.service.StudyTagService;
import com.web.stard.domain.tag.domain.entity.Tag;
import com.web.stard.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudyTagServiceImpl implements StudyTagService {

    private final StudyTagRepository studyTagRepository;
    private final TagRepository tagRepository;

    @Override
    public void createStudyTags(Study study) {
        List<String> tagTexts = Arrays.stream(study.getTagText().split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty()).toList();

        List<Tag> existingTags = tagRepository.findByNameIn(tagTexts);
        List<StudyTag> studyTags = new ArrayList<>();

        tagTexts.forEach(tagText -> {
            Tag tag = existingTags.stream().filter(existingTag ->
                            existingTag.getName().equals(tagText))
                    .findFirst()
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(tagText).build()));

            StudyTag studyTag = StudyTag.builder()
                    .study(study)
                    .tag(tag).build();
            studyTags.add(studyTag);
        });
        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addTags(savedStudyTags);
    }

}
