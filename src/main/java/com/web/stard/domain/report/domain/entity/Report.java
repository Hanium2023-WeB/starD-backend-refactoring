package com.web.stard.domain.report.domain.entity;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.reply.domain.enums.ReportReason;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long targetId;  // 신고 대상 id

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;  // 게시글 타입

    @Enumerated(EnumType.STRING)
    @Column(name = "report_reason", nullable = false)
    private ReportReason reportReason;  // 신고 사유

    @Column(name = "custom_reason")
    private String customReason;    // 기타 사유(사용자 입력)

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
