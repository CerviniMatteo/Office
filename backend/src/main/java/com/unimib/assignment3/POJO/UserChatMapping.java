package com.unimib.assignment3.POJO;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;
    @Entity
public class UserChatMapping {

    @Id
    private Long userId;

    @ElementCollection
    private List<Long> roomIds;

    public UserChatMapping() {
        roomIds = new ArrayList<>();
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public void setRoomIds(List<Long> roomIds) {
        this.roomIds = roomIds;
    }

    public void setRoomId(Long roomId){
        this.roomIds.add(roomId);
    }
}
