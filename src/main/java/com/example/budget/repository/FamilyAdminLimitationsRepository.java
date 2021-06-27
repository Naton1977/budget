package com.example.budget.repository;

import com.example.budget.domain.entity.FamilyAdminLimitations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyAdminLimitationsRepository extends JpaRepository<FamilyAdminLimitations, Integer> {
    FamilyAdminLimitations findByFamilyMemberId(int memberId);
}
