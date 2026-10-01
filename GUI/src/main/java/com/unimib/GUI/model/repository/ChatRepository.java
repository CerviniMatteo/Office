package com.unimib.GUI.model.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimib.GUI.UI.view.utils.FileUtils;
import com.unimib.GUI.model.controller.ChatSocketController;
import com.unimib.GUI.model.controller.impl.ChatRestController;
import com.unimib.GUI.model.dto.ChatInfoDTO;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.model.dto.WorkerInfoDTO;
import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class ChatRepository {

    private final ChatRestController restDataSource;
    private final ChatSocketController socketDataSource;
    private final ObjectMapper mapper;

    private final Map<Long, List<MessageDTO>> cache = new HashMap<>();

    private final List<Consumer<Long>> cacheListeners = new ArrayList<>();

    // una cartella per utente: due utenti sulla stessa macchina
    // non condividono più i file di cache
    private final Path baseDir;

    public ChatRepository(Long employeeId) {

        baseDir = Path.of(
                System.getProperty("user.home"),
                ".chat-cache",
                String.valueOf(employeeId)
        );

        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        restDataSource = new ChatRestController();
        socketDataSource = new ChatSocketController();
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        socketDataSource.addReceiveListener(json -> {
            try {

                MessageDTO message =
                        mapper.readValue(json, MessageDTO.class);

                cacheMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<ChatInfoDTO> getChats(Long employeeId) {
        return restDataSource.getChats(employeeId);
    }

    public List<WorkerInfoDTO> getUnMatchedEmployeeInfos(Long employeeId) {
        return restDataSource.getUnMatchedEmployeeInfos(employeeId);
    }

    public Task<Void> connect() {
        return socketDataSource.connect();
    }

    public Task<Void> disconnect() {
        return socketDataSource.disconnect();
    }

    public Task<Void> sendMessage(MessageDTO message) {
        return socketDataSource.sendMessage(message);
    }


    public void cacheMessage(MessageDTO message) {

        synchronized (this) {

            List<MessageDTO> messages =
                    cache.computeIfAbsent(message.chatId(), this::loadMessagesFromDisk);

            // deduplica: se l'id è già presente non scrivo e non notifico
            boolean alreadyPresent = messages.stream()
                    .anyMatch(m -> m.id().equals(message.id()));

            if (alreadyPresent)
                return;

            messages.add(message);

            FileUtils.appendObject(
                    baseDir.resolve(message.chatId() + ".txt"),
                    message
            );
        }

        notifyCacheChanged(message.chatId());
    }

    public List<MessageDTO> loadMessages(Long chatId) {
        synchronized (this) {
            return new ArrayList<>(
                    cache.computeIfAbsent(chatId, this::loadMessagesFromDisk)
            );
        }
    }

    private List<MessageDTO> loadMessagesFromDisk(Long chatId) {
        return new ArrayList<>(
                FileUtils.readObjects(
                        baseDir.resolve(chatId + ".txt"),
                        MessageDTO.class
                )
        );
    }

    public void addCacheListener(Consumer<Long> listener) {
        synchronized (cacheListeners) {
            cacheListeners.add(listener);
        }
    }

    public void removeCacheListener(Consumer<Long> listener) {
        synchronized (cacheListeners) {
            cacheListeners.remove(listener);
        }
    }

    private void notifyCacheChanged(Long chatId) {

        List<Consumer<Long>> snapshot;

        synchronized (cacheListeners) {
            snapshot = new ArrayList<>(cacheListeners);
        }

        for (Consumer<Long> listener : snapshot) {
            try {
                listener.accept(chatId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Task<Void> createChat(Long employeeId1, Long employeeId2) {
        return restDataSource.createChat(employeeId1, employeeId2);
    }
}