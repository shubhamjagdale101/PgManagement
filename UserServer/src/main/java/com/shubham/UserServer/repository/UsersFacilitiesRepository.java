package com.shubham.UserServer.repository;

import com.shubham.UserServer.model.Facilities;
import com.shubham.UserServer.model.User;
import com.shubham.UserServer.model.UsersFacilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersFacilitiesRepository extends JpaRepository<UsersFacilities, Integer> {
    @Query("SELECT uf FROM UsersFacilities uf WHERE uf.user = :user AND uf.facility = :facility")
    UsersFacilities findByUserAndFacility(User user, Facilities facility);
}
