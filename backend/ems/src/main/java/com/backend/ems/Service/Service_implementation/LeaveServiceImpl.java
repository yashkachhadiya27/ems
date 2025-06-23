package com.backend.ems.Service.Service_implementation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.backend.ems.DTO.LeaveDto;
import com.backend.ems.DTO.LeaveRequestDTO;
import com.backend.ems.DTO.LeaveResponseDTO;
import com.backend.ems.Entity.Leave;
import com.backend.ems.Entity.Notification;
import com.backend.ems.Entity.Register;
import com.backend.ems.Repository.LeaveRequestRepository;
import com.backend.ems.Repository.NotificationRepository;
import com.backend.ems.Service.Service_Interface.LeaveServiceInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveServiceInterface {
    private final LeaveRequestRepository leaveRequestRepository;
    private final RegisterServiceImpl registerServiceImpl;
    private final NotificationRepository notificationRepository;
    private final EmailServiceImpl emailServiceImpl;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");

    @Override
    @Transactional
    public void submitLeaveRequest(LeaveRequestDTO lrd, String email, int leaveId) {
        int userId;
        LocalDate leaveFromDate = lrd.getLeaveFromDate();
        LocalDate leaveToDate = lrd.getLeaveToDate();

        long totalDaysRequested = ChronoUnit.DAYS.between(leaveFromDate, leaveToDate) + 1;
        Leave leaveRequest;

        if (leaveId == -1) {
            Register r = registerServiceImpl.employeeByEmail(email);
            if (leaveRequestRepository.existsByRegisterIdAndLeaveDatesOverlap(r.getId(),
                    leaveFromDate, leaveToDate) > 0) {
                throw new Error("Dates Are overlapped");
            }

            leaveRequest = new Leave();
            leaveRequest.setRegister(r);
            userId = r.getId();
        } else {
            leaveRequest = leaveRequestRepository.findById(leaveId).get();
            leaveRequest.setWfhLeavesTaken(0);
            leaveRequest.setRegularLeavesTaken(0);
            leaveRequest.setCarryOverLeaves(0);
            leaveRequest.setLossOfPayDays(0);
            leaveRequestRepository.save(leaveRequest);
            userId = leaveRequest.getRegister().getId();
        }
        leaveRequest.setReason(lrd.getReason());
        leaveRequest.setLeaveFromDate(leaveFromDate);
        leaveRequest.setLeaveToDate(leaveToDate);
        leaveRequest.setLeaveType(lrd.getLeaveType());
        leaveRequest.setStatus("Pending");
        leaveRequest.setAppliedOn(LocalDate.now());
        if ("Work From Home".equalsIgnoreCase(lrd.getLeaveType())) {
            if (((int) totalDaysRequested > 2 || (int) totalDaysRequested < 0) || !canApplyForWFH(userId)) {
                throw new IllegalArgumentException("You do not have enough Leaves with Pay for this quarter.");
            } else {
                leaveRequest.setWfhLeavesTaken((int) totalDaysRequested);
            }
        } else if ("Loss of Pay".equalsIgnoreCase(lrd.getLeaveType())) {
            leaveRequest.setLossOfPayDays((int) totalDaysRequested);
        } else if ("Leaves with Pay".equalsIgnoreCase(lrd.getLeaveType())) {

            int carryOverLeaves = calculateCarryOverLeaves(userId, (int) totalDaysRequested);
            leaveRequest.setCarryOverLeaves(carryOverLeaves);
            leaveRequest.setRegularLeavesTaken((int) totalDaysRequested);
        } else {
            int restrictedLeave = getRemainingRestrictedLeaves(userId);
            if (restrictedLeave <= 0 || totalDaysRequested > 2) {
                throw new IllegalArgumentException("You do not have enough Leaves with Pay for this quarter.");
            }
        }

        leaveRequestRepository.save(leaveRequest);

    }

    @Override
    public void updateLeaveRequest(LeaveRequestDTO lrd, int leaveId) {
        Leave l = leaveRequestRepository.findById(leaveId).get();
        l.setReason(lrd.getReason());
        l.setLeaveFromDate(lrd.getLeaveFromDate());
        l.setLeaveToDate(lrd.getLeaveToDate());
        l.setLeaveType(lrd.getLeaveType());
        leaveRequestRepository.save(l);

    }

    private int calculateCarryOverLeaves(int registerId, int totalDaysRequested) {
        int quarterlyLeaveQuota = 4;

        Register register = registerServiceImpl.employeeById(registerId);
        LocalDate joiningDate = register.getDateOfJoining();

        int carryOverFromPreviousQuarter = getCarryOverLeaves(registerId, joiningDate);
        int leavesTakenInCurrentQuarter = getLeavesUsedInCurrentQuarter(registerId);

        int totalLeavesThisQuarter = leavesTakenInCurrentQuarter + totalDaysRequested;

        int totalAvailableLeaves = quarterlyLeaveQuota + carryOverFromPreviousQuarter;

        if (totalLeavesThisQuarter > totalAvailableLeaves) {
            return totalAvailableLeaves - totalLeavesThisQuarter;
            // return carryOverFromPreviousQuarter - (totalLeavesThisQuarter -
            // totalAvailableLeaves);
        } else {
            return carryOverFromPreviousQuarter + (quarterlyLeaveQuota - totalLeavesThisQuarter);
        }
    }

    @Override
    public Page<LeaveResponseDTO> getAllLeave(Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<LeaveResponseDTO> leaveResp = leaveRequestRepository.getAllLeave(pageable);
        leaveResp.forEach((l) -> {
            l.setImage("http://localhost:9090/admin/getUserImage/" + l.getImage());
        });
        return leaveResp;
    }

    @Override
    public Page<LeaveResponseDTO> getAllPendingLeave(Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<LeaveResponseDTO> leaveResp = leaveRequestRepository.getAllPendingLeave(pageable);
        leaveResp.forEach((l) -> {
            l.setImage("http://localhost:9090/admin/getUserImage/" + l.getImage());
        });
        return leaveResp;
    }

    @Override
    public Leave approveLeave(int id) {
        Leave al = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        al.setStatus("Approved");
        al.setReplyOn(LocalDate.now());
        Leave l = leaveRequestRepository.save(al);
        return l;

    }

    @Override
    public Leave rejectLeave(int id) {
        Leave al = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        al.setStatus("Rejected");
        al.setReplyOn(LocalDate.now());
        Leave l = leaveRequestRepository.save(al);
        return l;

    }

    @Async("asyncTaskExecutor")
    public void sendMailAndAddNotification(Leave al, String status) {
        Notification nfn = new Notification();
        if (status == "Approve") {
            nfn.setRegisterId(al.getRegister().getId());
            nfn.setMessage("Leave Approved From " + al.getLeaveFromDate().format(formatter) + " to "
                    + al.getLeaveToDate().format(formatter) + ".");
            notificationRepository.save(nfn);
            String body = bodyForLeaveStatus(al.getRegister().getFname(), status,
                    al.getLeaveFromDate(), al.getLeaveToDate(), "green");
            emailServiceImpl.sendEmail(null, al.getRegister().getEmail(), null, null, "Leave Status", body);
        } else {
            nfn.setRegisterId(al.getRegister().getId());
            nfn.setMessage("Leave Rejected From " + al.getLeaveFromDate().format(formatter) + " to "
                    + al.getLeaveToDate().format(formatter) + ".");
            notificationRepository.save(nfn);
            String body = bodyForLeaveStatus(al.getRegister().getFname(), status,
                    al.getLeaveFromDate(), al.getLeaveToDate(), "green");
            emailServiceImpl.sendEmail(null, al.getRegister().getEmail(), null, null, "Leave Status", body);
        }
    }

    @Override
    public Page<LeaveResponseDTO> getAllSearchedLeave(String keyword, Integer pageNumber, Integer pageSize,
            String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<LeaveResponseDTO> leaveResp = leaveRequestRepository.getAllSearchedLeave(keyword, pageable);
        leaveResp.forEach((l) -> {
            l.setImage("http://localhost:9090/admin/getUserImage/" + l.getImage());
        });
        return leaveResp;
    }

    @Override
    public Page<LeaveResponseDTO> getAllSearchedPendingLeave(String keyword, Integer pageNumber, Integer pageSize,
            String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<LeaveResponseDTO> leaveResp = leaveRequestRepository.getAllSearchedPendingLeave(keyword, pageable);
        leaveResp.forEach((l) -> {
            l.setImage("http://localhost:9090/admin/getUserImage/" + l.getImage());
        });
        return leaveResp;
    }

    public String bodyForLeaveStatus(String name, String status, LocalDate fromDate, LocalDate toDate, String color) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String emailTemplate = ""
                + "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head>"
                + "    <meta charset=\"UTF-8\">"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "    <title>Leave Status Notification</title>"
                + "    <style>"
                + "        body {"
                + "            font-family: Arial, sans-serif;"
                + "            background-color: #f4f4f4;"
                + "            margin: 0;"
                + "            padding: 0;"
                + "        }"
                + "        .container {"
                + "            background-color: #ffffff;"
                + "            margin: 30px auto;"
                + "            padding: 20px;"
                + "            max-width: 600px;"
                + "            border-radius: 10px;"
                + "            box-shadow: 0 4px 8px rgba(0,0,0,0.1);"
                + "        }"
                + "        .header {"
                + "            text-align: center;"
                + "            background-color: #4CAF50;"
                + "            padding: 10px;"
                + "            color: white;"
                + "            border-top-left-radius: 10px;"
                + "            border-top-right-radius: 10px;"
                + "        }"
                + "        .content {"
                + "            margin-top: 20px;"
                + "        }"
                + "        .content p {"
                + "            font-size: 16px;"
                + "            line-height: 1.6;"
                + "        }"
                + "        .footer {"
                + "            margin-top: 30px;"
                + "            text-align: center;"
                + "            font-size: 12px;"
                + "            color: #666666;"
                + "        }"
                + "        .status {"
                + "            font-weight: bold;"
                + "            color: " + color + ""
                + "        }"
                + "        .approved {"
                + "            color: #4CAF50;"
                + "        }"
                + "        .list-item {"
                + "            margin-bottom:5px"
                + "        }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class=\"container\">"
                + "        <div class=\"header\">"
                + "            <h2>Leave Status Update</h2>"
                + "        </div>"
                + "        <div class=\"content\">"
                + "            <p>Dear <strong>" + name + "</strong>,</p>"
                + "            <p>We would like to inform you that your leave request has been <span class=\"status\">"
                + status.toUpperCase() + "</span>.</p>"
                + "            <p>Below are the details of your leave request:</p>"
                + "            <ul>"
                + "                <li class=\"list-item\"><strong>From Date:</strong> " + fromDate.format(formatter)
                + "</li>"
                + "                <li><strong>To Date:</strong> " + toDate.format(formatter) + "</li>"
                + "            </ul>"
                + "            <p>If you have any questions or need further clarification, feel free to contact the HR department.</p>"
                + "            <p>Best regards,</p>"
                + "            <p><strong>HR Department</strong></p>"
                + "        </div>"
                + "        <div class=\"footer\">"
                + "            <p>This is an automated message. Please do not reply to this email.</p>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>";
        return emailTemplate;

    }

    @Override
    public Page<LeaveResponseDTO> getAllLeaveOfEmployee(int userId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<LeaveResponseDTO> leaveResp = leaveRequestRepository.findByRegisterId(userId, pageable);
        return leaveResp;
    }

    @Override
    public int totalPendingLeave() {
        return leaveRequestRepository.totalPendingLeave();
    }

    @Override
    public int totalPendingLeaveOfEmployee(int registerId) {
        return leaveRequestRepository.totalPendingLeaveOfEmployee(registerId);
    }

    public boolean canApplyForWFH(int registerId) {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        long wfhLeavesThisMonth = leaveRequestRepository.countWFHLeaveDaysInRange(registerId, firstDayOfMonth,
                lastDayOfMonth);
        if (wfhLeavesThisMonth <= 0) {
            wfhLeavesThisMonth = 0L;
        }

        return wfhLeavesThisMonth < 2;
    }

    public int getRemainingLeavesForQuarter(int registerId) {
        Register register = registerServiceImpl.employeeById(registerId);
        LocalDate joiningDate = register.getDateOfJoining();
        LocalDate now = LocalDate.now();

        LocalDate startOfQuarter = calculateQuarterStart(joiningDate, now);
        int carryOverLeaves = getCarryOverLeaves(registerId, joiningDate);
        long totalLeavesTaken = leaveRequestRepository.countLeavesInRange(registerId, startOfQuarter, now);
        int totalAvailableLeaves = 4 + carryOverLeaves;
        int remainingLeaves = totalAvailableLeaves - (int) totalLeavesTaken;

        if (totalLeavesTaken > totalAvailableLeaves) {
            int excessLeaves = (int) totalLeavesTaken - totalAvailableLeaves;
            carryOverLeaves -= excessLeaves;
        }

        updateCarryOverLeaves(registerId, carryOverLeaves);

        return remainingLeaves;

    }

    private void updateCarryOverLeaves(int registerId, int carryOverLeaves) {
        List<Leave> leaves = leaveRequestRepository.findLeaveByRegisterId(registerId);
        if (!leaves.isEmpty()) {
            Leave latestLeave = leaves.get(leaves.size() - 1);
            latestLeave.setCarryOverLeaves(carryOverLeaves);
            leaveRequestRepository.save(latestLeave);
        } else {
            Leave newLeave = new Leave();
            newLeave.setRegister(registerServiceImpl.employeeById(registerId));
            newLeave.setCarryOverLeaves(carryOverLeaves);
            leaveRequestRepository.save(newLeave);
        }
    }

    private LocalDate calculateQuarterStart(LocalDate joiningDate, LocalDate now) {
        if (joiningDate.isAfter(now)) {
            throw new IllegalArgumentException("Joining date cannot be in the future.");
        }

        int month = now.getMonthValue();
        int year = now.getYear();
        LocalDate quarterStart;

        if (month >= 1 && month <= 3) {
            quarterStart = LocalDate.of(year, 1, 1); // Q1
        } else if (month >= 4 && month <= 6) {
            quarterStart = LocalDate.of(year, 4, 1); // Q2
        } else if (month >= 7 && month <= 9) {
            quarterStart = LocalDate.of(year, 7, 1); // Q3
        } else {
            quarterStart = LocalDate.of(year, 10, 1); // Q4
        }

        if (joiningDate.isAfter(quarterStart.plusMonths(3).minusDays(1))) {
            quarterStart = quarterStart.plusMonths(3);
        }

        return quarterStart;

    }

    private int getCarryOverLeaves(int registerId, LocalDate joiningDate) {
        LocalDate now = LocalDate.now();
        LocalDate startOfCurrentQuarter = calculateQuarterStart(joiningDate, now);
        List<Leave> previousLeaves = leaveRequestRepository.findLeaveByRegisterIdBeforeDate(registerId,
                startOfCurrentQuarter);
        int totalCarryOver = previousLeaves.stream()
                .mapToInt(Leave::getCarryOverLeaves)
                .sum();
        return totalCarryOver;

    }

    public int getWFHLeavesRemaining(int registerId) {
        long wfhLeavesThisMonth = leaveRequestRepository.countWFHLeaveDaysInRange(registerId,
                LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        return 2 - (int) wfhLeavesThisMonth;
    }

    public int getRemainingRestrictedLeaves(int registerId) {
        int restrictedHolidayQuota = 2;
        long takenRestrictedLeaves = leaveRequestRepository.countRestrictedLeavesTaken(registerId);

        return restrictedHolidayQuota - (int) takenRestrictedLeaves;
    }

    public int getTotalLeavesUsedForYear(int registerId) {
        Register register = registerServiceImpl.employeeById(registerId);
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);

        return (int) leaveRequestRepository.countLeavesInRange(registerId, startOfYear, now);
    }

    public int getLeavesUsedInCurrentQuarter(int registerId) {
        Register register = registerServiceImpl.employeeById(registerId);
        LocalDate now = LocalDate.now();
        LocalDate startOfQuarter = calculateQuarterStart(register.getDateOfJoining(), now);

        return (int) leaveRequestRepository.countLeavesInRange(registerId, startOfQuarter, now);
    }

    public int calculateTotalLeaves(int registerId) {
        Register register = registerServiceImpl.employeeById(registerId);
        LocalDate dateOfJoining = register.getDateOfJoining();
        LocalDate now = LocalDate.now();

        if (dateOfJoining.isAfter(now)) {
            throw new IllegalArgumentException("Joining date cannot be in the future.");
        }

        int quarterlyLeaveQuota = 4;
        int totalQuartersPassed = 0;
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;

        if (dateOfJoining.getYear() == now.getYear()) {
            int joiningQuarter = (dateOfJoining.getMonthValue() - 1) / 3 + 1;
            totalQuartersPassed = currentQuarter - joiningQuarter + 1;
        } else {
            int yearDiff = now.getYear() - dateOfJoining.getYear();
            totalQuartersPassed += yearDiff * 4;
            totalQuartersPassed += currentQuarter;
        }
        return totalQuartersPassed * quarterlyLeaveQuota;
    }

    public int getRemainingLeaves(int registerId) {
        Register register = registerServiceImpl.employeeById(registerId);
        LocalDate joiningDate = register.getDateOfJoining();
        LocalDate now = LocalDate.now();
        LocalDate startOfCurrentQuarter = calculateQuarterStart(joiningDate, now);

        List<Leave> previousLeaves = leaveRequestRepository.findLeaveByRegisterIdBeforeDate(registerId,
                startOfCurrentQuarter);

        List<Leave> currentQuarterLeaves = leaveRequestRepository.findLeaveByRegisterIdInRange(registerId,
                startOfCurrentQuarter, now);

        int totalLeavesTakenBeforeCurrentQuarter = previousLeaves.stream()
                .mapToInt(Leave::getRegularLeavesTaken)
                .sum();

        int totalLeavesTakenInCurrentQuarter = currentQuarterLeaves.stream()
                .mapToInt(Leave::getRegularLeavesTaken)
                .sum();

        int completedQuarters = getCompletedQuarters(joiningDate, now);

        int fullLeaveQuota = (completedQuarters + 1) * 4;

        int totalLeavesTaken = totalLeavesTakenBeforeCurrentQuarter + totalLeavesTakenInCurrentQuarter;

        return fullLeaveQuota - totalLeavesTaken;
    }

    private int getCompletedQuarters(LocalDate joiningDate, LocalDate currentDate) {
        int joiningYear = joiningDate.getYear();
        int currentYear = currentDate.getYear();

        int startQuarter = (joiningDate.getMonthValue() - 1) / 3 + 1;

        int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1;

        if (joiningYear == currentYear) {
            return currentQuarter - startQuarter;
        } else {

            int remainingQuartersInJoiningYear = 4 - startQuarter + 1;
            return remainingQuartersInJoiningYear + (currentQuarter - 1) + (currentYear - joiningYear - 1) * 4;
        }
    }

    public List<LeaveDto> getEmployeesOnLeaveForDate(LocalDate recordDate) {
        List<Leave> leaveRecords = leaveRequestRepository.findEmployeesOnLeaveForDate(recordDate);
        return leaveRecords.stream()
                .map(this::convertToLeaveDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveDto> getEmployeesOnWFHForDate(LocalDate recordDate) {
        List<Leave> wfhRecords = leaveRequestRepository.findEmployeesOnWFHForDate(recordDate);
        return wfhRecords.stream()
                .map(this::convertToLeaveDTO)
                .collect(Collectors.toList());
    }

    private LeaveDto convertToLeaveDTO(Leave leave) {
        LeaveDto dto = new LeaveDto();
        dto.setFullName(leave.getRegister().getFname() + " " + leave.getRegister().getLname());
        dto.setImage("http://localhost:9090/adminEmployee/getUserImage/" + leave.getRegister().getImage());
        dto.setLeaveType(leave.getLeaveType());
        dto.setLeaveFromDate(leave.getLeaveFromDate());
        dto.setLeaveToDate(leave.getLeaveToDate());
        return dto;
    }

    @Transactional
    @Override
    public void deleteLeaveById(int leaveId) {
        leaveRequestRepository.deleteById(leaveId);
    }
}
