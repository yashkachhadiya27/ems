package com.backend.ems.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.backend.ems.Entity.Notification;

import jakarta.transaction.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByRegisterId(int registerId);

    @Transactional
    @Modifying
    void deleteAllByRegisterId(int notificationId);

}
