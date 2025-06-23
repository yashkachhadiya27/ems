package com.backend.ems.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.backend.ems.DTO.MessageDto;
import com.backend.ems.DTO.MessageGroupDto;
import com.backend.ems.DTO.MessageRequestDto;
import com.backend.ems.Entity.ChatRoom;
import com.backend.ems.Entity.Message;
import com.backend.ems.Entity.Register;
import com.backend.ems.Enums.UserStatus;
import com.backend.ems.Exception.EmployeeNotFoundException;
import com.backend.ems.Repository.ChatRoomRepository;
import com.backend.ems.Repository.MessageRepository;
import com.backend.ems.Repository.RegisterRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FileStorageService fileStorageService;
    private final RegisterRepository registerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Message saveMessage(int roomId, MessageRequestDto messageDto) throws IOException {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new Error("Chat room not found"));
        Register sender = registerRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new EmployeeNotFoundException("Sender not found"));
        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(messageDto.getContent());
        message.setContentType(messageDto.getContentType());
        message.setTimestamp(LocalDateTime.now());

        if (messageDto.getFilePath() != null && !messageDto.getFilePath().isEmpty()) {
            message.setFilePath(messageDto.getFilePath());
        }
        Message savedMessage = messageRepository.save(message);

        messagingTemplate.convertAndSend("/topic/" + roomId, savedMessage);

        return savedMessage;
    }

    @Transactional
    public Message editMessage(int messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        message.setContent(newContent);
        message.setEdited(true);
        message.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/" + message.getChatRoom().getId(), message);
        return messageRepository.save(message);
    }

    @Transactional
    public Message deleteMessage(int messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        message.setDeleted(true);
        message.setContent("This message has been deleted");
        message.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/" + message.getChatRoom().getId(), message);
        return messageRepository.save(message);
    }

    public void updateUserStatus(int userId, UserStatus status) {
        Register user = registerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(status);
        registerRepository.save(user);
    }

    public List<?> getMessagesByRoomId(int roomId) {
        String type = chatRoomRepository.findChatRoomTypeById(roomId);
        if (type == "PRIVATE") {

            return messageRepository.findMessagesByChatRoomId(roomId);
        } else {
            return messageRepository.findGroupMessagesByChatRoomId(roomId);
        }
    }

}
