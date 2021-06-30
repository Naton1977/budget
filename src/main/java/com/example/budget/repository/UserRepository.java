package com.example.budget.repository;


import com.example.budget.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findFamilyMemberByFamily_FamilyId(int id);

    User findFamilyMemberByMemberLogin(String memberLogin);

}
