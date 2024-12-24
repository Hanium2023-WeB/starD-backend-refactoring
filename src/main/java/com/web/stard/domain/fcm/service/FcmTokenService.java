package com.web.stard.domain.fcm.service;

import com.web.stard.domain.member.domain.entity.Member;

public interface FcmTokenService {

    void register(Member member, String fcmToken);

    void remove(Member member);
}
