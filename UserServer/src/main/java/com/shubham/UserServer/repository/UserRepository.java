package com.shubham.UserServer.repository;

import com.shubham.UserServer.model.User;
import jakarta.annotation.Nullable;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUserName(String username);
}
