package com.backend.ems.Entity;

import java.time.LocalDateTime;

import com.backend.ems.Enums.ChatRoomType;
import com.backend.ems.Enums.MessageType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JsonIgnore
    private Register sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private MessageType contentType;
    private LocalDateTime timestamp;

    private Boolean edited = false;
    private Boolean deleted = false;

    @ManyToOne
    private ChatRoom chatRoom;

    private String filePath;

}
