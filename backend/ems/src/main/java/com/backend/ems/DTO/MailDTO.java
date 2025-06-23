package com.backend.ems.DTO;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class MailDTO {
    private MultipartFile[] file;
    private String to;
    private String[] cc;
    private String[] bcc;
    private String subject;
    private String body;
}
