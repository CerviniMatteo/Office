package com.unimib.assignment3.POJO;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UnreadMessages {
    @Id
    private Long roomId;

    @ElementCollection
    private List<String> messages;

    public UnreadMessages(Long roomId){
        setRoomId(roomId);
        messages = new ArrayList<>();
    }

    public UnreadMessages() {

    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessage(String message) {
        this.messages.add(message);
    }

    public void removeMessages(){
        this.messages.clear();
    }

    public void removeMessage(String message){
        this.messages.remove(message);
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "UnreadMessages{" +
                "roomId=" + roomId +
                ", messages=" + messages +
                '}';
    }
}
