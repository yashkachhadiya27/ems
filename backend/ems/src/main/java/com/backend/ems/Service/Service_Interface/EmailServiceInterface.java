package com.backend.ems.Service.Service_Interface;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface EmailServiceInterface {
    Map<String, Object> sendEmail(MultipartFile[] file, String to, String[] cc, String[] bcc, String subject,
            String body);
}
