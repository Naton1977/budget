package com.example.budget.repository;

import com.example.budget.domain.entity.GlobalAdminAllFamilyLimitations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalAdminAllFamilyLimitationsRepository extends JpaRepository<GlobalAdminAllFamilyLimitations, Integer> {
    GlobalAdminAllFamilyLimitations findByFamilyId(int familyId);
}
