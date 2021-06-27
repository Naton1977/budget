package com.example.budget.repository;


import com.example.budget.domain.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, Integer> {

    Family findFamilyByFamilyLogin(String login);

}
