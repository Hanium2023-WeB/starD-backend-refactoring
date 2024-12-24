package com.web.stard.domain.fcm.service.impl;

import com.web.stard.domain.fcm.domain.entity.FcmToken;
import com.web.stard.domain.fcm.repository.FcmTokenRepository;
import com.web.stard.domain.fcm.service.FcmTokenService;
import com.web.stard.domain.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    /**
     * FCM 토큰 등록
     *
     * @param member   회원 정보
     * @param fcmToken FCM 토큰
     */
    @Override
    public void register(Member member, String fcmToken) {
        fcmTokenRepository.save(FcmToken.builder().member(member).fcmToken(fcmToken).build());
    }

    /**
     * FCM 토큰 삭제
     *
     * @param member 회원 정보
     */
    @Override
    @Transactional
    public void remove(Member member) {
        fcmTokenRepository.deleteByMember(member);
    }
}
