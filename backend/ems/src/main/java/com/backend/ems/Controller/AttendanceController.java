package com.backend.ems.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.AbsentEmployeeDto;
import com.backend.ems.DTO.AttendanceDto;
import com.backend.ems.DTO.AveragesDto;
import com.backend.ems.DTO.CheckInOutDto;
import com.backend.ems.Entity.Attendance;
import com.backend.ems.Service.Service_implementation.AttendanceServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceServiceImpl attendanceService;

    @GetMapping("/public/test")
    public String test() {
        return "Neej Patel ...................";
    }

    @GetMapping("/employee/attendance/status/{userId}")
    public ResponseEntity<Boolean> isCheckedIn(@PathVariable int userId) {
        boolean isCheckedIn = attendanceService.isUserCheckedIn(userId);
        return ResponseEntity.ok(isCheckedIn);
    }

    @PostMapping("/employee/attendance/in/{userId}")
    public ResponseEntity<Attendance> markIn(@PathVariable int userId) {
        Attendance attendance = attendanceService.markIn(userId);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/employee/attendance/out/{userId}")
    public ResponseEntity<Attendance> markOut(@PathVariable int userId) {
        Attendance attendance = attendanceService.markOut(userId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/employee/attendance/attedanceByMonth/{userId}")
    public List<AttendanceDto> getAttendanceByMonth(@PathVariable int userId, @RequestParam("month") int month,
            @RequestParam("year") int year) {
        return attendanceService.getMonthlyAttendance(userId, month, year);
    }

    @GetMapping("/employee/attendance/averagesForMonth/{userId}")
    public AveragesDto getAveragesForMonth(@PathVariable int userId, @RequestParam("month") int month,
            @RequestParam("year") int year) {
        return attendanceService.getAveragesForMonth(userId, month, year);
    }

    @GetMapping("/employee/attendance/logs/{userId}")
    public List<CheckInOutDto> getLogsForDate(@PathVariable int userId, @RequestParam("date") String date) {
        LocalDate logDate = LocalDate.parse(date);
        return attendanceService.getLogsForDay(userId, logDate);
    }

    @GetMapping("/adminEmployee/attendance/employees-absent")
    public ResponseEntity<List<AbsentEmployeeDto>> getEmployeesNotPresentDate(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate recordDate;
        if (date == null) {
            recordDate = LocalDate.now();
        } else {

            recordDate = date;
        }
        return ResponseEntity.status(200).body(attendanceService.getEmployeesNotCheckedInDate(recordDate));
    }

}
