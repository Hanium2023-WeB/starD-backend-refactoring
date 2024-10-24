package com.web.stard.domain.member.repository;

import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findAllByMember(Member member);
}
