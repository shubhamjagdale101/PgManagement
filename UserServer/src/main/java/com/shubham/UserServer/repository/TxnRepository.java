package com.shubham.UserServer.repository;

import com.shubham.UserServer.model.MonthyRent;
import com.shubham.UserServer.model.Txn;
import com.shubham.UserServer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TxnRepository extends JpaRepository<Txn, Integer> {
    @Query("SELECT t FROM Txn t WHERE t.user = :user AND t.month = :month")
    Txn findTxn(User user, MonthyRent month);
}
