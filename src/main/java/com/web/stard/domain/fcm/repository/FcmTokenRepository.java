package com.web.stard.domain.fcm.repository;

import com.web.stard.domain.fcm.domain.entity.FcmToken;
import com.web.stard.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    void deleteByMember(Member member);
}
