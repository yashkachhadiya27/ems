package com.backend.ems.Service.Service_Interface;

import java.util.List;

import com.backend.ems.Entity.Notice;

public interface NoticeServiceInterface {
    public Notice addNotice(Notice notice);

    public List<Notice> getActiveNotices();
}
