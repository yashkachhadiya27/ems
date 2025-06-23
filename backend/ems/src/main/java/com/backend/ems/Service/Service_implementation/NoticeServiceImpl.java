package com.backend.ems.Service.Service_implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.ems.Entity.Notice;
import com.backend.ems.Entity.Notification;
import com.backend.ems.Enums.NoticeType;
import com.backend.ems.Repository.NoticeRepository;
import com.backend.ems.Service.Service_Interface.NoticeServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeServiceInterface {
    private final NoticeRepository noticeRepository;

    @Override
    public Notice addNotice(Notice notice) {

        return noticeRepository.save(notice);
    }

    @Override
    public List<Notice> getActiveNotices() {
        return noticeRepository.findAllByDeadlineAfter(LocalDate.now());
    }

}
