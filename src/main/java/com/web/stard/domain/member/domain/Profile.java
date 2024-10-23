package com.web.stard.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault("5.0")
    private double credibility; // 신뢰도

    private String introduce;   // 자기소개

    @Column(name = "img_url")
    private String imgUrl;  // 이미지 경로
}
