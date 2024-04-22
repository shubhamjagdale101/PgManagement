package com.shubham.UserServer.repository;

import com.shubham.UserServer.model.Message;
import com.shubham.UserServer.model.MessageType;
import com.shubham.UserServer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT msg FROM Message msg WHERE msg.type = :type OR msg.sender = :user OR msg.receiver = :user ORDER BY msg.creationDate DESC")
    Page<Message> getMessage(MessageType type, User user, Pageable pageable);
}
