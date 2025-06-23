package com.backend.ems.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.MessageDto;
import com.backend.ems.DTO.MessageGroupDto;
import com.backend.ems.Entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT new com.backend.ems.DTO.MessageDto(m.id, m.sender.id, m.chatRoom.id, m.content, m.timestamp, m.contentType, m.filePath, m.deleted, m.edited,m.chatRoom.type) FROM Message m WHERE m.chatRoom.id = :chatRoomId AND m.deleted=false ORDER BY m.id")
    List<MessageDto> findMessagesByChatRoomId(@Param("chatRoomId") int chatRoomId);

    @Query("SELECT new com.backend.ems.DTO.MessageGroupDto(m.id, m.sender.id, m.chatRoom.id, m.content, m.timestamp, m.contentType, m.filePath, m.deleted, m.edited,m.chatRoom.type,CONCAT(m.sender.fname,' ',m.sender.lname),m.sender.phone) FROM Message m WHERE m.chatRoom.id = :chatRoomId AND m.deleted=false ORDER BY m.id")
    List<MessageGroupDto> findGroupMessagesByChatRoomId(@Param("chatRoomId") int chatRoomId);
}
