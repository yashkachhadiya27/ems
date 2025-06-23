package com.backend.ems.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.UserChatInfoDto;
import com.backend.ems.Entity.ChatRoom;
import com.backend.ems.Enums.ChatRoomType;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
        List<ChatRoom> findByUsers_Id(Long userId);

        @Query("SELECT c FROM ChatRoom c JOIN c.users u WHERE u.id IN :userIds GROUP BY c HAVING COUNT(u) = :userCount")
        Optional<ChatRoom> findChatRoomByUserIds(@Param("userIds") List<Integer> userIds,
                        @Param("userCount") long userCount);

        @Query("SELECT cr FROM ChatRoom cr JOIN cr.users u WHERE u.id = :userId")
        List<ChatRoom> findChatRoomsByUserId(@Param("userId") int userId);

        @Query("SELECT cr FROM ChatRoom cr JOIN cr.users u WHERE u.id = :userId AND cr.type = :type")
        List<ChatRoom> findGroupChatRoomsByUserId(@Param("userId") int userId, ChatRoomType type);

        @Query("SELECT new com.backend.ems.DTO.UserChatInfoDto(u.id, u.image, CONCAT(u.fname,' ',u.lname), u.department, u.status) "
                        +
                        "FROM ChatRoom cr " +
                        "JOIN cr.users u " +
                        "WHERE cr.id IN (SELECT cr2.id FROM ChatRoom cr2 JOIN cr2.users u2 WHERE u2.id = :userId) " +
                        "AND u.id <> :userId AND cr.type=:type " +
                        "GROUP BY u.id, u.image, CONCAT(u.fname,' ',u.lname), u.department, u.status")
        List<UserChatInfoDto> findContactsByUserId(@Param("userId") int userId, @Param("type") ChatRoomType type);

        @Query("select c.type from ChatRoom c where id=:id")
        String findChatRoomTypeById(@Param("id") int chatRoomId);
}
