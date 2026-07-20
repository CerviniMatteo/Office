package com.unimib.GUI.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimib.GUI.UI.view.utils.FileUtils;
import com.unimib.GUI.model.controller.ChatSocketController;
import com.unimib.GUI.model.controller.impl.ChatRestController;
import com.unimib.GUI.model.dto.MessageDTO;
import javafx.concurrent.Task;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class ChatRepository {

    private final ChatRestController restDataSource;
    private final ChatSocketController socketDataSource;
    private final ObjectMapper mapper;

    private final Map<Long, List<MessageDTO>> cache = new HashMap<>();
    private final List<Consumer<Long>> cacheListeners = new ArrayList<>();

    private final Path baseDir =
            Path.of(System.getProperty("user.home"), ".chat-cache");

    public ChatRepository() {

        restDataSource = new ChatRestController();
        socketDataSource = new ChatSocketController();
        mapper = new ObjectMapper();

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

    public Task<List<Long>> getChats(Long employeeId) {
        return restDataSource.getChats(employeeId);
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

    public synchronized void cacheMessage(MessageDTO message) {

        cache.computeIfAbsent(
                message.chatId(),
                this::loadMessagesFromDisk
        ).add(message);

        FileUtils.appendObject(
                baseDir.resolve(message.chatId() + ".txt"),
                message
        );

        notifyCacheChanged(message.chatId());
    }

    public synchronized List<MessageDTO> loadMessages(Long chatId) {

        return new ArrayList<>(
                cache.computeIfAbsent(
                        chatId,
                        this::loadMessagesFromDisk
                )
        );
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
        cacheListeners.add(listener);
    }

    public void removeCacheListener(Consumer<Long> listener) {
        cacheListeners.remove(listener);
    }

    private void notifyCacheChanged(Long chatId) {
        cacheListeners.forEach(listener -> listener.accept(chatId));
    }
}