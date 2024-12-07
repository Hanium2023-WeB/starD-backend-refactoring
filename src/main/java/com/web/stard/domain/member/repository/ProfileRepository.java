package com.web.stard.domain.member.repository;

import com.web.stard.domain.member.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
