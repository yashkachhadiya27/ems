package com.backend.ems.Service.Service_implementation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.ems.Service.Service_Interface.EmailServiceInterface;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailServiceInterface {
    @Value("${spring.mail.username}")
    private String fromMail;
    private final JavaMailSender javaMailSender;

    @Override
    public Map<String, Object> sendEmail(MultipartFile[] file, String to, String[] cc, String[] bcc, String subject,
            String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(fromMail);
            mimeMessageHelper.setTo(to);
            if (cc != null && cc.length > 0) {
                mimeMessageHelper.setCc(cc);
            }
            if (bcc != null && bcc.length > 0) {
                mimeMessageHelper.setBcc(bcc);

            }
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
            if (file != null) {
                for (int i = 0; i < file.length; i++) {
                    mimeMessageHelper.addAttachment(file[i].getOriginalFilename(),
                            new ByteArrayResource(file[i].getBytes()));
                }
            }
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "mail sent successfully");
            responseData.put("from", fromMail);
            responseData.put("to", to);
            javaMailSender.send(mimeMessage);
            return responseData;
        } catch (Exception e) {
            throw new RuntimeException("Error Occured while sending an Email");
        }

    }

}
