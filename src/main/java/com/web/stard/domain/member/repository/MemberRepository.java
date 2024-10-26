package com.web.stard.domain.member.repository;

import com.web.stard.domain.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

  Optional<Member> findByEmail(String email);

}
