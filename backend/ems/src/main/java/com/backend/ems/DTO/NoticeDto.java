package com.backend.ems.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDto {
    private int id;
    private String title;
    private String content;
    private LocalDate deadline;
}
