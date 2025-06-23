package com.backend.ems.Service.Service_implementation;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.ems.DTO.AbsentEmployeeDto;
import com.backend.ems.DTO.AttendanceDto;
import com.backend.ems.DTO.AveragesDto;
import com.backend.ems.DTO.CheckInOutDto;
import com.backend.ems.Entity.Attendance;
import com.backend.ems.Entity.CheckInOut;
import com.backend.ems.Entity.Register;
import com.backend.ems.Repository.AttendanceRepository;
import com.backend.ems.Repository.CheckInOutRepository;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.Service_Interface.AttendanceServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceServiceInterface {
    private final AttendanceRepository attendanceRepository;
    private final CheckInOutRepository checkInOutRepository;
    private final RegisterRepository registerRepository;

    @Override
    public boolean isUserCheckedIn(int userId) {
        Attendance attendance = getAttendanceForToday(userId);
        if (attendance != null) {
            if (attendance.getLastCheckInTime() != null && attendance.getAttendanceDate().isEqual(LocalDate.now())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Attendance markIn(int userId) {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = getAttendanceForToday(userId);

        CheckInOut checkInOut = new CheckInOut();
        checkInOut.setAttendance(attendance);
        checkInOut.setCheckInTime(now);

        attendance.setLastCheckInTime(now);
        attendance.setPresent(true);

        checkInOutRepository.save(checkInOut);
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance markOut(int userId) {
        Attendance attendance = getAttendanceForToday(userId);
        LocalDateTime now = LocalDateTime.now();

        CheckInOut checkInOut = checkInOutRepository.findFirstByAttendanceAndCheckOutTimeIsNull(attendance)
                .orElseThrow(() -> new RuntimeException("No ongoing check-in found"));

        checkInOut.setCheckOutTime(now);
        Duration workedTime = Duration.between(checkInOut.getCheckInTime(), now);
        checkInOut.setDuration(workedTime);

        attendance.setTotalInTime(attendance.getTotalInTime().plus(workedTime));
        attendance.setLastCheckInTime(null);

        checkInOutRepository.save(checkInOut);
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance getAttendanceForToday(int userId) {
        return attendanceRepository.findByRegisterIdAndAttendanceDate(userId, LocalDate.now())
                .orElseGet(() -> createNewAttendance(userId));
    }

    private Attendance createNewAttendance(int userId) {
        Attendance attendance = new Attendance();
        attendance.setRegister(registerRepository.findById(userId).orElseThrow());
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setPresent(false);
        attendance.setTotalInTime(Duration.ZERO);
        return attendanceRepository.save(attendance);
    }

    @Override
    public void autoCheckOut(int userId) {
        Attendance attendance = getAttendanceForToday(userId);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 30)); // 6:30 PM

        CheckInOut lastCheckInOut = checkInOutRepository.findFirstByAttendanceAndCheckOutTimeIsNull(attendance)
                .orElse(null);

        if (lastCheckInOut != null) {
            lastCheckInOut.setCheckOutTime(endOfDay);
            Duration workedTime = Duration.between(lastCheckInOut.getCheckInTime(), endOfDay);
            lastCheckInOut.setDuration(workedTime);
            attendance.setTotalInTime(attendance.getTotalInTime().plus(workedTime));
            checkInOutRepository.save(lastCheckInOut);
        }

        attendanceRepository.save(attendance);
    }

    @Override
    public List<AttendanceDto> getMonthlyAttendance(int userId, int month, int year) {
        return attendanceRepository.findByMonth(userId, month, year);
    }

    @Override
    public AveragesDto getAveragesForMonth(int userId, int month, int year) {
        return attendanceRepository.calculateAveragesForMonth(userId, month, year);
    }

    @Override
    public List<CheckInOutDto> getLogsForDay(int userId, LocalDate date) {
        return checkInOutRepository.findByDate(userId, date);
    }

    public List<AbsentEmployeeDto> getEmployeesNotCheckedInDate(LocalDate recordDate) {
        List<Register> absentEmployees = attendanceRepository.findEmployeesNotCheckedInForDate(recordDate);
        return absentEmployees.stream()
                .map(this::convertToAbsentEmployeeDTO)
                .collect(Collectors.toList());
    }

    private AbsentEmployeeDto convertToAbsentEmployeeDTO(Register employee) {
        AbsentEmployeeDto dto = new AbsentEmployeeDto();
        dto.setFullName(employee.getFname() + " " + employee.getLname());
        dto.setImage("http://localhost:9090/adminEmployee/getUserImage/" + employee.getImage());
        return dto;
    }
}
