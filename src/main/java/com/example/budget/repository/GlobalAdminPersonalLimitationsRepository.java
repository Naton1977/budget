package com.example.budget.repository;

import com.example.budget.domain.entity.GlobalAdminPersonalLimitations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalAdminPersonalLimitationsRepository extends JpaRepository<GlobalAdminPersonalLimitations, Integer> {

    GlobalAdminPersonalLimitations findByMemberId(int memberId);
}
