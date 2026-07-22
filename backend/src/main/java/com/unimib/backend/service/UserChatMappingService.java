package com.unimib.backend.service;

import com.unimib.backend.DTO.ChatInfoDTO;
import com.unimib.backend.DTO.UserInfoDTO;
import com.unimib.backend.POJO.UserChatMapping;
import com.unimib.backend.repository.UserChatMappingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
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

    public void bindChat(@NonNull Long userId1, @NonNull Long userId2) {
        assertNotNull(userId1, "User id 1 cannot be null");
        assertNotNull(userId2, "User id 2 cannot be null");

        long roomId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        UserChatMapping userChatMapping1 = userChatMappingRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User mapping not found for id: " + userId1));
        UserChatMapping userChatMapping2 = userChatMappingRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User mapping not found for id: " + userId2));

        userChatMapping1.setRoomId(roomId);
        userChatMapping2.setRoomId(roomId);
    }


    public UserChatMapping saveChat(@NonNull UserChatMapping mapping) {
        assertNotNull(mapping, "UserChatMapping cannot be null");
        return userChatMappingRepository.saveAndFlush(mapping);
    }

    public List<ChatInfoDTO> findChatInfoByUserId(@NonNull Long userId) {
        assertNotNull(userId, "User id cannot be null");
        return userChatMappingRepository.findRoomInfoByUserId(userId);
    }

    protected void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public List<UserInfoDTO> findUnMatchedEmployeeIds(Long employeeId) {
        return userChatMappingRepository.findUnmatchedUserIds(employeeId);
    }
}

