package com.shubham.UserServer.repository;

import com.shubham.UserServer.model.MonthyRent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyRentRepository extends JpaRepository<MonthyRent, Integer> {
    MonthyRent findByMonthYear(String monthYear);
}
