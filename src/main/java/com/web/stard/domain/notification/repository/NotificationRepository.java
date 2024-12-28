package com.web.stard.domain.notification.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.notification.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverOrderByIdDesc(Member member, Pageable pageable);
}
