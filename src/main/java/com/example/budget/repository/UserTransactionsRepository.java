package com.example.budget.repository;

import com.example.budget.domain.entity.UserTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface UserTransactionsRepository extends JpaRepository<UserTransactions, Integer> {

    List<UserTransactions> findAllByDateTransactionAndTypeOfTransaction(Date date, String type);

}
