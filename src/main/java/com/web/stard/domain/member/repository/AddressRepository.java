package com.web.stard.domain.member.repository;

import com.web.stard.domain.member.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
