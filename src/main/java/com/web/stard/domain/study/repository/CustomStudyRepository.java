package com.web.stard.domain.study.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.web.stard.domain.member.domain.entity.QMember;
import com.web.stard.domain.member.domain.entity.QProfile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.study.domain.dto.request.StudyRequestDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.QStudy;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomStudyRepository {

    private final JPAQueryFactory queryFactory;
    private final QStudy qStudy = QStudy.study;
    private final QMember qMember = QMember.member;

    public Page<StudyResponseDto.StudyInfo> searchStudiesWithFilter(StudyRequestDto.StudySearchFilter filter,
                                                                    Pageable pageable) {
        List<StudyResponseDto.StudyInfo> content = queryFactory.select(Projections.fields(StudyResponseDto.StudyInfo.class,
                        qStudy.title, qStudy.hit, qStudy.activityType, qStudy.recruitmentType,
                        qStudy.tagText, qStudy.activityStart, qStudy.activityDeadline,
                        qStudy.recruitmentDeadline, qStudy.city, qStudy.district,
                        qStudy.field, qMember.nickname, qMember.profile.imgUrl,
                        qStudy.id.as("studyId")))
                .from(qStudy)
                .join(qStudy.member, qMember)
                .join(qMember.profile, QProfile.profile)
                .orderBy(qStudy.id.desc())
                .where(recruitmentTypeEq(filter.recruitmentType())
                        , keywordContains(filter.keyword())
                        , activityTypeEq(filter.activityType())
                        , tagsEq(filter.tags())
                        , fieldEq(filter.field())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(qStudy.count())
                .from(qStudy)
                .where(recruitmentTypeEq(filter.recruitmentType()),
                        keywordContains(filter.keyword()),
                        activityTypeEq(filter.activityType()),
                        tagsEq(filter.tags()),
                        fieldEq(filter.field())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression recruitmentTypeEq(RecruitmentType recruitmentType) {
        return recruitmentType == null ?
                null : qStudy.recruitmentType.eq(recruitmentType);
    }

    private BooleanExpression keywordContains(String keyword) {
        return keyword != null ?
                qStudy.title.contains(keyword).or(qStudy.content.contains(keyword)) : null;
    }

    private BooleanExpression tagsEq(String tagText) {
        return tagText == null ? null : Arrays.stream(tagText.split(",")).toList()
                .stream()
                .map(tag -> qStudy.tagText.contains(tag))
                .reduce(BooleanExpression::or).orElse(null);
    }

    private BooleanExpression fieldEq(InterestField field) {
        return field != null ?
                qStudy.field.eq(field) : null;
    }

    private BooleanExpression activityTypeEq(ActivityType activityType) {
        return activityType != null ?
                qStudy.activityType.eq(activityType) : null;
    }

}
