package com.backend.ems.Entity;

import java.util.List;

import com.backend.ems.Enums.ChatRoomType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private ChatRoomType type;

    @ManyToMany
    @JoinTable(name = "chatroom_users", joinColumns = @JoinColumn(name = "chatroom_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Register> users;

}
