package com.backend.ems.Scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.backend.ems.Entity.Attendance;
import com.backend.ems.Entity.CheckInOut;
import com.backend.ems.Entity.Leave;
import com.backend.ems.Entity.Register;
import com.backend.ems.Repository.AttendanceRepository;
import com.backend.ems.Repository.CheckInOutRepository;
import com.backend.ems.Repository.LeaveRequestRepository;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.Service_implementation.AttendanceServiceImpl;
import com.backend.ems.Service.Service_implementation.EmailServiceImpl;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceServiceImpl attendanceService;

    private final RegisterRepository registerRepository;

    private final AttendanceRepository attendanceRepository;

    private final CheckInOutRepository checkInOutRepository;

    private final LeaveRequestRepository leaveRepository;

    private final EmailServiceImpl emailServiceImpl;

    @Scheduled(cron = "0 30 23 * * MON-SAT")
    public void autoCheckOut() {
        List<Register> allUsers = registerRepository.findAll();
        for (Register user : allUsers) {
            Attendance attendance = attendanceService.getAttendanceForToday(user.getId());
            if (attendance != null) {
                if (attendance.getLastCheckInTime() != null) {
                    attendanceService.autoCheckOut(user.getId());
                    attendance = attendanceService.getAttendanceForToday(user.getId());
                }

                Long totalBreakTime = calculateTotalBreakTime(attendance);
                attendance.setTotalBreakTime(Duration.ofSeconds(totalBreakTime));
                attendance.setLastCheckInTime(null);

                attendanceRepository.save(attendance);
            }
        }
    }

    @Scheduled(cron = "0 30 23 * * TUE-SUN")
    public void markAbsentAndNotify() {
        LocalDate today = LocalDate.now();

        List<Register> absentEmployees = attendanceRepository.findEmployeesNotCheckedInForDate(today);

        List<Leave> employeesOnLeave = leaveRepository.findEmployeesOnLeaveForDate(today);
        List<Leave> employeesOnWFH = leaveRepository.findEmployeesOnWFHForDate(today);

        Set<Integer> onLeaveOrWFHEmployeeIds = new HashSet<>();
        employeesOnLeave.forEach(leave -> onLeaveOrWFHEmployeeIds.add(leave.getRegister().getId()));
        employeesOnWFH.forEach(wfh -> onLeaveOrWFHEmployeeIds.add(wfh.getRegister().getId()));

        List<Register> absentWithoutLeave = absentEmployees.stream()
                .filter(employee -> !onLeaveOrWFHEmployeeIds.contains(employee.getId()))
                .collect(Collectors.toList());

        if (!absentWithoutLeave.isEmpty()) {
            sendAbsenceNotification(absentWithoutLeave);
        }
    }

    private void sendAbsenceNotification(List<Register> absentEmployees) {
        absentEmployees.forEach(employee -> {
            emailServiceImpl.sendEmail(null, employee.getEmail(), null, null, "Absent status", "You are absent Today!");

        });
    }

    private Long calculateTotalBreakTime(Attendance attendance) {
        List<CheckInOut> checkInOuts = checkInOutRepository.findByAttendance(attendance);
        long totalBreakTime = 0;

        for (int i = 0; i < checkInOuts.size() - 1; i++) {
            CheckInOut current = checkInOuts.get(i);
            CheckInOut next = checkInOuts.get(i + 1);

            if (current.getCheckOutTime() != null && next.getCheckInTime() != null) {
                Duration breakDuration = Duration.between(current.getCheckOutTime(), next.getCheckInTime());
                totalBreakTime += breakDuration.getSeconds();
            }
        }

        return totalBreakTime;
    }

}
