package com.web.stard.domain.member.domain;

import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.apache.catalina.User;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    private String email;   // 이메일

    private String password;    // 비밀번호

    private String name;    // 이름

    private String nickname;    // 닉네임

    private String phone;   // 전화번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 권한

    @ColumnDefault("false")
    @Column(name = "matching_study_allow", columnDefinition = "TINYINT(1)")
    private boolean matchingStudyAllow; // 스터디 매칭 알림 여부

    @ColumnDefault("0")
    @Column(name = "report_count")
    private double reportCount; // 누적 신고 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
