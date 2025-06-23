package com.backend.ems.Service.Service_Interface;

import java.time.LocalDate;
import java.util.List;

import com.backend.ems.DTO.AttendanceDto;
import com.backend.ems.DTO.AveragesDto;
import com.backend.ems.DTO.CheckInOutDto;
import com.backend.ems.Entity.Attendance;

public interface AttendanceServiceInterface {
    public boolean isUserCheckedIn(int userId);

    public Attendance markIn(int userId);

    public Attendance markOut(int userId);

    public Attendance getAttendanceForToday(int userId);

    public void autoCheckOut(int userId);

    public List<AttendanceDto> getMonthlyAttendance(int userId, int month, int year);

    public AveragesDto getAveragesForMonth(int userId, int month, int year);

    public List<CheckInOutDto> getLogsForDay(int userId, LocalDate date);
}
