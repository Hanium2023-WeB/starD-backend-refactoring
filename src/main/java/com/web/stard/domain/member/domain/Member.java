package com.web.stard.domain.member.domain;

import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    private String email;   // 이메일

    private String password;    // 비밀번호

    private String nickname;    // 닉네임

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 권한

    @ColumnDefault("true")
    @Column(name = "matching_study_allow", columnDefinition = "TINYINT(1)")
    private boolean matchingStudyAllow; // 스터디 매칭 알림 여부

    @ColumnDefault("0")
    @Column(name = "report_count")
    private double reportCount; // 누적 신고 수

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();
}
