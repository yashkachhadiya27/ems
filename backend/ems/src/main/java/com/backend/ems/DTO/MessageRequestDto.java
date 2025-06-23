package com.backend.ems.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.backend.ems.Enums.MessageType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequestDto {
    private int id;
    private int senderId;
    private int chatRoomId;
    private String content;
    private LocalDateTime timestamp;
    private MessageType contentType;
    private String filePath;
    private boolean deleted;
    private boolean edited;

}
