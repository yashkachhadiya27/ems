package com.backend.ems.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.Entity.Notification;
import com.backend.ems.Service.Service_implementation.NotificationServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class NotificationController {
    private final NotificationServiceImpl notificationServiceImpl;

    @GetMapping("/notificationForEmp/{registerId}")
    public ResponseEntity<List<Notification>> getNotificationForEmp(@PathVariable int registerId) {
        return ResponseEntity.status(200).body(notificationServiceImpl.getNotificationForEmp(registerId));
    }

    @DeleteMapping("/removeNotification/{registerId}")
    public ResponseEntity<CustomResponse> removeNotification(@PathVariable int registerId) {
        notificationServiceImpl.removeNotification(registerId);
        return ResponseEntity.status(200).body(new CustomResponse("Success", 200));
    }
}
