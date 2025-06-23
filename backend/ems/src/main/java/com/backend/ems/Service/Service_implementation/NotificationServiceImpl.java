package com.backend.ems.Service.Service_implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.ems.Entity.Notification;
import com.backend.ems.Repository.NotificationRepository;
import com.backend.ems.Service.Service_Interface.NotificationServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationServiceInterface {
    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getNotificationForEmp(int registerId) {
        return notificationRepository.findByRegisterId(registerId);
    }

    @Override
    public void removeNotification(int registerId) {
        notificationRepository.deleteAllByRegisterId(registerId);
    }

}
