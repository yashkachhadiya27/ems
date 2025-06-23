package com.backend.ems.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.ems.DTO.ChatRoomDto;
import com.backend.ems.DTO.UserChatInfoDto;
import com.backend.ems.Entity.ChatRoom;
import com.backend.ems.Entity.Register;
import com.backend.ems.Enums.ChatRoomType;
import com.backend.ems.Enums.UserStatus;
import com.backend.ems.Repository.ChatRoomRepository;
import com.backend.ems.Repository.RegisterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final RegisterRepository registerRepository;

    public ChatRoom createChatRoom(String name, ChatRoomType type, List<Integer> userIds) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findChatRoomByUserIds(userIds, userIds.size());

        if (existingRoom.isPresent() && existingRoom.get().getType() == type) {
            return existingRoom.get();
        }
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoom.setType(type);
        chatRoom.setUsers(new ArrayList<>());

        for (Integer userId : userIds) {
            Register user = registerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            chatRoom.getUsers().add(user);
        }

        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom addUsersToChatRoom(int chatRoomId, List<Integer> userIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        for (Integer userId : userIds) {
            Register user = registerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (!chatRoom.getUsers().contains(user)) {
                chatRoom.getUsers().add(user);
            }
        }

        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom removeUsersFromChatRoom(int chatRoomId, List<Integer> userIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        for (Integer userId : userIds) {
            Register user = registerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            chatRoom.getUsers().remove(user);
        }

        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom getChatRoomById(int chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public boolean isUserInChatRoom(int chatRoomId, int userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        return chatRoom.getUsers().stream().anyMatch(user -> user.getId() == userId);
    }

    public List<ChatRoom> getChatRoomsByUserId(int userId) {

        return chatRoomRepository.findChatRoomsByUserId(userId);
    }

    public List<ChatRoom> getGroupChatRoomsByUserId(int userId) {
        String typeString = "GROUP";
        ChatRoomType type = ChatRoomType.valueOf(typeString);
        return chatRoomRepository.findGroupChatRoomsByUserId(userId, type);
    }

    public List<UserChatInfoDto> getContacts(int userId) {
        String typeString = "PRIVATE";
        ChatRoomType type = ChatRoomType.valueOf(typeString);
        List<UserChatInfoDto> users = chatRoomRepository.findContactsByUserId(userId, type);
        users.forEach((u) -> {
            u.setImage("http://localhost:9090/adminEmployee/getUserImage/" + u.getImage());
        });
        return users;
    }

}
