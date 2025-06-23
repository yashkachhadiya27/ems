package com.backend.ems.Scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.backend.ems.DTO.BirthdayDto;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.Service_implementation.EmailServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BirthdayScheduler {
    private final RegisterRepository registerRepository;
    private final EmailServiceImpl emailServiceImpl;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBirthdayWishes() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        List<BirthdayDto> usersWithBirthday = registerRepository.findByDateOfBirthMonthAndDayForEmail(month, day);

        for (BirthdayDto user : usersWithBirthday) {
            String to = user.getEmail();
            String subject = "🎉 Happy Birthday, " + user.getFullname() + "! 🎉";
            String body = getBirthdayEmailContent(user.getFullname());
            emailServiceImpl.sendEmail(null, to, null, null, subject, body);
        }
    }

    private String getBirthdayEmailContent(String userName) {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><title>Happy Birthday</title>"
                + "<style>body{font-family:Arial,sans-serif;background-color:#f4f4f4;color:#333;margin:0;padding:0}.email-container{max-width:600px;margin:20px auto;"
                + "background-color:#ffffff;padding:20px;border-radius:8px;box-shadow:0 4px 8px rgba(0,0,0,0.1)}.header{background-color:#4CAF50;padding:10px;text-align:center;color:#ffffff}"
                + ".content{margin:20px}h1{color:#333333;font-size:24px}.footer{text-align:center;margin-top:20px;font-size:12px;color:#777}.wish{font-size:18px;color:#555}</style>"
                + "</head><body><div class='email-container'><div class='header'><h2>Happy Birthday, " + userName
                + " 🎉</h2></div><div class='content'>"
                + "<p class='wish'>Dear " + userName
                + ",</p><p>On behalf of everyone here at <strong>Argusoft</strong>, we want to take a moment to wish you a <strong>very happy birthday</strong>! 🎂🎈</p>"
                + "<p>We hope this day brings you <strong>joy</strong>, <strong>laughter</strong>, and wonderful moments with family and friends. May this year ahead be filled with <strong>success</strong>, "
                + "<strong>good health</strong>, and everything you’ve been hoping for.</p><p>Enjoy your special day to the fullest! 🌟</p></div><div class='footer'>"
                + "<p>Warmest wishes,</p><p><strong>The Argusoft Team</strong></p></div></div></body></html>";
    }
}
