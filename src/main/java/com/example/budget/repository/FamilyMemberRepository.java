package com.example.budget.repository;


import com.example.budget.domain.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Integer> {

    List<FamilyMember> findFamilyMemberByFamily_FamilyId(int id);

    FamilyMember findFamilyMemberByMemberLogin(String memberLogin);
}
