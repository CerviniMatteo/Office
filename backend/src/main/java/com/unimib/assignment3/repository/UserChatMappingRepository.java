package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.UserChatMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;

import java.util.List;

public interface UserChatMappingRepository extends JpaRepository<UserChatMapping, Long> {
    @Query("SELECT u.roomIds FROM UserChatMapping u WHERE u.userId = :userId")
    List<Long> findRoomIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT new org.springframework.data.util.Pair(u1.userId, u2.userId) FROM UserChatMapping u1, UserChatMapping u2 WHERE :roomId MEMBER OF u1.roomIds AND :roomId MEMBER OF u2.roomIds AND u1.userId <> u2.userId")
    Pair<Long, Long> findUserIdsByRoomId(@Param("roomId") Long roomId);
}
