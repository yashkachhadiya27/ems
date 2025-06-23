package com.backend.ems.Service.Service_Interface;

import java.util.List;

import com.backend.ems.Entity.Notification;

public interface NotificationServiceInterface {
    public List<Notification> getNotificationForEmp(int registerId);

    public void removeNotification(int registerId);
}
