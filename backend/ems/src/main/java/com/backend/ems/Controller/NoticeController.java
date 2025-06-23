package com.backend.ems.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.NoticeDto;
import com.backend.ems.Entity.Notice;
import com.backend.ems.Service.Service_implementation.NoticeServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeServiceImpl noticeServiceImpl;

    @PostMapping("/admin/addNotice")
    public Notice addNotice(@RequestBody NoticeDto noticeDTO) {
        Notice notice = new Notice();
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        notice.setDeadline(noticeDTO.getDeadline());
        return noticeServiceImpl.addNotice(notice);
    }

    @GetMapping("/adminEmployee/getActiveNotices")
    public List<Notice> getActiveNotices() {
        return noticeServiceImpl.getActiveNotices();
    }
}
