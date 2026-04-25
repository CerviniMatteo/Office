package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.UserChatMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserChatMappingRepository extends JpaRepository<UserChatMapping, Long> {
    @Query("SELECT u.roomIds FROM UserChatMapping u WHERE u.userId = :userId")
    List<Long> findRoomIdsByUserId(@Param("userId") Long userId);
}