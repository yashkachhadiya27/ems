package com.backend.ems.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.MailDTO;
import com.backend.ems.Service.Service_implementation.EmailServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class MailController {
    private final EmailServiceImpl emailServiceImpl;

    @PostMapping("/sendMail")
    public Map<String, Object> sendEmail(@ModelAttribute MailDTO mail) {
        return emailServiceImpl.sendEmail(mail.getFile(), mail.getTo(), mail.getCc(), mail.getBcc(), mail.getSubject(),
                mail.getBody());
    }
}
