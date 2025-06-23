package com.backend.ems.DTO;

import java.time.LocalDateTime;

import com.backend.ems.Enums.ChatRoomType;
import com.backend.ems.Enums.MessageType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDto {
    private int id;
    private int senderId;
    private int chatRoomId;
    private String content;
    private LocalDateTime timestamp;
    private MessageType contentType;
    private String filePath;
    private boolean deleted;
    private boolean edited;
    private ChatRoomType type;

    public MessageDto(int id, int senderId, int chatRoomId, String content, LocalDateTime timestamp,
            MessageType contentType, String filePath, boolean deleted, boolean edited, ChatRoomType type) {
        this.id = id;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.timestamp = timestamp;
        this.contentType = contentType;
        this.filePath = filePath;
        this.deleted = deleted;
        this.edited = edited;
        this.type = type;
    }

}
