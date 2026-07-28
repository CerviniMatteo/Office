package com.unimib.backend.repository;

import com.unimib.backend.DTO.ChatInfoDTO;
import com.unimib.backend.DTO.UserInfoDTO;
import com.unimib.backend.POJO.WorkerChatMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;

import java.util.List;

public interface UserChatMappingRepository extends JpaRepository<WorkerChatMapping, Long> {

    @Query("""
    SELECT DISTINCT new com.unimib.backend.DTO.ChatInfoDTO(
        r1,
        CONCAT(w.name, ' ', w.surname)
    )
    FROM WorkerChatMapping uc1
    JOIN uc1.roomIds r1
    CROSS JOIN WorkerChatMapping uc2
    JOIN uc2.roomIds r2
    JOIN worker w ON w.workerId = uc2.userId
    WHERE uc1.userId = :userId
      AND uc2.userId <> :userId
      AND r1 = r2
    """)
    List<ChatInfoDTO> findRoomInfoByUserId(@Param("userId") Long userId);

    @Query("SELECT new org.springframework.data.util.Pair(u1.userId, u2.userId) FROM WorkerChatMapping u1, WorkerChatMapping u2 WHERE :roomId MEMBER OF u1.roomIds AND :roomId MEMBER OF u2.roomIds AND u1.userId <> u2.userId")
    Pair<Long, Long> findUserIdsByRoomId(@Param("roomId") Long roomId);

    @Query("""
        SELECT DISTINCT new com.unimib.backend.DTO.UserInfoDTO(
        u.userId,
        CONCAT(w.name, ' ', w.surname)
    )
    FROM WorkerChatMapping u
    JOIN worker w ON w.workerId = u.userId
    WHERE u.userId <> :userId
      AND NOT EXISTS (
          SELECT r1
          FROM WorkerChatMapping me
          JOIN me.roomIds r1
          WHERE me.userId = :userId
            AND r1 IN (
                SELECT r2
                FROM u.roomIds r2
            )
      )
""")
    List<UserInfoDTO> findUnmatchedUserIds(@Param("userId") Long userId);
}


