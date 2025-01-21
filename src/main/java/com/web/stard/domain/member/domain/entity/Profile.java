package com.web.stard.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Long id;

    @Column
    private double credibility; // 신뢰도

    private String introduce;   // 자기소개

    @Column(name = "img_url")
    private String imgUrl;  // 이미지 경로

    public void updateImageUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void deleteImageUrl() {
        this.imgUrl = null;
    }

    public void updateCredibility(double starRating, int evaluatorCount) {
        /* starRating = 평가에 반영할 평점
           evaluatorCount = 기존 평가 인원수
           (기존 평점 * 인원 + 매긴 점수) / (인원 + 1)
           인원 + 1인 이유 : 기본 값 5점 */
        double total = this.credibility * evaluatorCount + starRating;
        this.credibility = total / (evaluatorCount + 1);
    }

    public void updateIntroduce(String introduce) {
        if (!this.introduce.equals(introduce)) {
            this.introduce = introduce;
        }
    }
}
