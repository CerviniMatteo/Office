package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.UnreadMessages;
import com.unimib.assignment3.repository.UnreadMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Service
public class UnreadMessagesService {

    @Autowired
    private UnreadMessagesRepository unreadMessagesRepository;

    public UnreadMessages createUnreadMessages(@NonNull Long roomId){
        return new UnreadMessages(roomId);
    }

    public UnreadMessages saveUnreadMessages(@NonNull UnreadMessages unreadMessages) {
        assertNotNull(unreadMessages, "UnreadMessages cannot be null");
        return unreadMessagesRepository.saveAndFlush(unreadMessages);
    }

    public UnreadMessages findOrCreateByRoomId(@NonNull Long roomId) {
        assertNotNull(roomId, "Room id cannot be null");
        return unreadMessagesRepository.findById(roomId)
                .orElseGet(() -> new UnreadMessages(roomId));
    }

    @Transactional
    public void appendMessage(@NonNull Long roomId, @NonNull String message) {
        assertNotNull(roomId, "Room id cannot be null");
        assertNotNull(message, "Message cannot be null");

        UnreadMessages unreadMessages = findOrCreateByRoomId(roomId);
        unreadMessages.setMessage(message);
        unreadMessagesRepository.saveAndFlush(unreadMessages);
    }

    @Transactional
    public void removeSingleMessage(@NonNull Long roomId, @NonNull String message) {
        assertNotNull(roomId, "Room id cannot be null");
        assertNotNull(message, "Message cannot be null");

        unreadMessagesRepository.findById(roomId).ifPresent(unreadMessages -> {
            unreadMessages.removeMessage(message);
            if (unreadMessages.getMessages().isEmpty()) {
                unreadMessagesRepository.deleteById(roomId);
            } else {
                unreadMessagesRepository.saveAndFlush(unreadMessages);
            }
        });
    }

    @Transactional
    public List<String> getMessages(@NonNull Long roomId) {
        assertNotNull(roomId, "Room id cannot be null");
        AtomicReference<List<String>> messages = new AtomicReference<>();
        unreadMessagesRepository.findById(roomId).ifPresent(unreadMessages -> {
            messages.set(unreadMessages.getMessages());
            unreadMessages.removeMessages();
        });
        return messages.get();
    }

}

