package com.shubham.UserServer.repository;

import com.shubham.UserServer.model.Facilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facilities, Integer> {
    public Facilities findByName(String facilityName);
}
