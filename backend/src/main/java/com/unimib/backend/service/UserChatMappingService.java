package com.unimib.backend.service;

import com.unimib.backend.POJO.UserChatMapping;
import com.unimib.backend.repository.UserChatMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserChatMappingService {

    @Autowired
    private UserChatMappingRepository userChatMappingRepository;

    public UserChatMapping createChat(@NonNull Long userId) {
        assertNotNull(userId, "User id cannot be null");

        UserChatMapping mapping = new UserChatMapping();
        mapping.setUserId(userId);
        mapping.setRoomIds(new ArrayList<>());

        return userChatMappingRepository.saveAndFlush(mapping);
    }


    public UserChatMapping saveChat(@NonNull UserChatMapping mapping) {
        assertNotNull(mapping, "UserChatMapping cannot be null");
        return userChatMappingRepository.saveAndFlush(mapping);
    }

    public List<Long> findRoomIdsByUserId(@NonNull Long userId) {
        assertNotNull(userId, "User id cannot be null");
        return userChatMappingRepository.findRoomIdsByUserId(userId);
    }

    public Pair<Long, Long> findUserIdsByRoomId(@NonNull Long roomId) {
        assertNotNull(roomId, "Room id cannot be null");
        return userChatMappingRepository.findUserIdsByRoomId(roomId);
    }

    protected void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public List<Long> findUnMatchedEmployeeIds(Long employeeId) {
        return userChatMappingRepository.findUnmatchedUserIds(employeeId);
    }
}

