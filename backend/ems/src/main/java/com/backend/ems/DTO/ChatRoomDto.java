package com.backend.ems.DTO;

import java.util.List;

import com.backend.ems.Enums.ChatRoomType;

import lombok.Data;

@Data
public class ChatRoomDto {
    private String name; // Name of the chat room
    private ChatRoomType type; // Type of chat room (e.g., GROUP, PRIVATE)
    private List<Integer> userIds; // IDs of users to be added to the chat room
}
