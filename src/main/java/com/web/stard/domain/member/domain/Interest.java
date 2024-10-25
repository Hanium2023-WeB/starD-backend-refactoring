package com.web.stard.domain.member.domain;

import com.web.stard.domain.member.domain.enums.InterestField;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_field", nullable = false)
    private InterestField interestField; // 관심 분야

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
