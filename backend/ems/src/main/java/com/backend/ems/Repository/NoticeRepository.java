package com.backend.ems.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.ems.Entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findAllByDeadlineAfter(LocalDate currentDate);
}
