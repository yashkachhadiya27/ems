package com.backend.ems.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.backend.ems.DTO.MessageDto;
import com.backend.ems.DTO.MessageRequestDto;
import com.backend.ems.DTO.UserChatInfoDto;
import com.backend.ems.Entity.ChatRoom;
import com.backend.ems.Entity.Message;
import com.backend.ems.Enums.ChatRoomType;
import com.backend.ems.Enums.UserStatus;
import com.backend.ems.Service.ChatRoomService;
import com.backend.ems.Service.FileStorageService;
import com.backend.ems.Service.MessageService;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final FileStorageService fileStorageService;
    private static final long MAX_IMAGE_SIZE = 15 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable int roomId, MessageRequestDto messageDto) throws IOException {

        Message message = messageService.saveMessage(roomId, messageDto);
        messagingTemplate.convertAndSend("/topic/" + roomId, message);
    }

    @MessageMapping("/chat/edit/{messageId}")
    public void editMessage(@DestinationVariable int messageId, String newContent) {
        Message updatedMessage = messageService.editMessage(messageId, newContent);
        messagingTemplate.convertAndSend("/topic/" + updatedMessage.getChatRoom().getId(), updatedMessage);
    }

    @MessageMapping("/chat/delete/{messageId}")
    public void deleteMessage(@DestinationVariable int messageId) {
        Message deletedMessage = messageService.deleteMessage(messageId);
        messagingTemplate.convertAndSend("/topic/" + deletedMessage.getChatRoom().getId(), deletedMessage);
    }

    @GetMapping("/api/chatroom/{chatRoomId}/messages")
    @ResponseBody
    public List<?> getMessagesForChatRoom(@PathVariable int chatRoomId) {
        return messageService.getMessagesByRoomId(chatRoomId);
    }

    @PostMapping("/api/chatroom/create")
    @ResponseBody
    public ChatRoom createChatRoom(@RequestParam String name, @RequestParam ChatRoomType type,
            @RequestParam List<Integer> userIds) {
        return chatRoomService.createChatRoom(name, type, userIds);
    }

    @PostMapping("/api/chatroom/{roomId}/add-users")
    @ResponseBody
    public ChatRoom addUsersToChatRoom(@PathVariable int roomId, @RequestBody List<Integer> userIds) {
        return chatRoomService.addUsersToChatRoom(roomId, userIds);
    }

    @PostMapping("/api/chatroom/{roomId}/remove-users")
    @ResponseBody
    public ChatRoom removeUsersFromChatRoom(@PathVariable int roomId, @RequestBody List<Integer> userIds) {
        return chatRoomService.removeUsersFromChatRoom(roomId, userIds);
    }

    @GetMapping("/api/chatroom/{roomId}")
    @ResponseBody
    public ChatRoom getChatRoomById(@PathVariable int roomId) {
        return chatRoomService.getChatRoomById(roomId);
    }

    @GetMapping("/api/chatrooms")
    @ResponseBody
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllChatRooms();
    }

    @GetMapping("/api/chatroom/{roomId}/user/{userId}")
    @ResponseBody
    public boolean isUserInChatRoom(@PathVariable int roomId, @PathVariable int userId) {
        return chatRoomService.isUserInChatRoom(roomId, userId);
    }

    @GetMapping("/api/chatrooms/user/{userId}")
    @ResponseBody
    public List<ChatRoom> getChatRoomsByUserId(@PathVariable int userId) {
        return chatRoomService.getChatRoomsByUserId(userId);
    }

    @GetMapping("/api/chatrooms/groups/user/{userId}")
    @ResponseBody
    public List<ChatRoom> getGroupChatRoomsByUserId(@PathVariable int userId) {
        return chatRoomService.getGroupChatRoomsByUserId(userId);
    }

    @MessageMapping("/user/status/{userId}")
    public void updateUserStatus(@DestinationVariable int userId, @RequestParam UserStatus status) {
        messageService.updateUserStatus(userId, status);
        messagingTemplate.convertAndSend("/topic/status/" + userId, status);
    }

    @PostMapping("/api/files/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("started to upload file>");
        String contentType = file.getContentType();
        try {
            if (contentType != null) {
                if (contentType.startsWith("image") && file.getSize() > MAX_IMAGE_SIZE) {
                    throw new MaxUploadSizeExceededException(MAX_IMAGE_SIZE);
                } else if (contentType.startsWith("video") && file.getSize() > MAX_VIDEO_SIZE) {
                    throw new MaxUploadSizeExceededException(MAX_VIDEO_SIZE);
                }
            }
            String filePath = fileStorageService.storeFile(file);
            Map<String, String> response = new HashMap<>();
            response.put("filePath", filePath);
            System.out.println("added success to upload file>");

            return ResponseEntity.ok(response);

        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File size exceeds the allowed limit.");
        }

    }

    @GetMapping("/api/files/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/api/chatroom/contacts/{userId}")
    @ResponseBody
    public List<UserChatInfoDto> getContacts(@PathVariable int userId) {
        return chatRoomService.getContacts(userId);
    }
}