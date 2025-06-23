package com.backend.ems.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.DTO.LeaveDashboardDTO;
import com.backend.ems.DTO.LeaveDto;
import com.backend.ems.DTO.LeaveRequestDTO;
import com.backend.ems.DTO.LeaveResponseDTO;
import com.backend.ems.Entity.Leave;
import com.backend.ems.Service.Service_implementation.LeaveServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveServiceImpl leaveServiceImpl;

    @PostMapping("/employee/leaveRequest/{email}")
    public ResponseEntity<CustomResponse> submitLeaveRequest(@PathVariable("email") String email,
            @ModelAttribute @Valid LeaveRequestDTO leaveRequestDTO) {
        int id = -1;
        try {
            System.out.println("here1");
            leaveServiceImpl.submitLeaveRequest(leaveRequestDTO, email, id);

            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("Fail", 400));
        }

    }

    @PatchMapping("/employee/updateLeave/{leaveId}")
    public ResponseEntity<CustomResponse> updateLeaveRequest(@PathVariable("leaveId") int leaveId,
            @ModelAttribute @Valid LeaveRequestDTO leaveRequestDTO) {
        String email = "";
        try {
            leaveServiceImpl.submitLeaveRequest(leaveRequestDTO, email, leaveId);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("Fail", 400));
        }

    }

    @DeleteMapping("/employee/deleteLeave/{leaveId}")
    public ResponseEntity<HttpStatus> deleteLeave(@PathVariable int leaveId) {
        leaveServiceImpl.deleteLeaveById(leaveId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/admin/getAllLeave")
    public ResponseEntity<Page<LeaveResponseDTO>> getAllLeave(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
        if (keyword.equals("")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(leaveServiceImpl.getAllLeave(pageNumber, pageSize, sortBy, sortOrder));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(leaveServiceImpl.getAllSearchedLeave(keyword, pageNumber, pageSize, sortBy, sortOrder));
        }

    }

    @GetMapping("/admin/getAllPendingLeave")
    public ResponseEntity<Page<LeaveResponseDTO>> getAllPendingLeave(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
        if (keyword.equals("")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(leaveServiceImpl.getAllPendingLeave(pageNumber, pageSize, sortBy, sortOrder));
        } else {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(leaveServiceImpl.getAllSearchedPendingLeave(keyword, pageNumber, pageSize, sortBy,
                            sortOrder));
        }
    }

    @PostMapping("/admin/approveLeave/{requestId}")
    public ResponseEntity<CustomResponse> approveLeaveRequest(@PathVariable int requestId) {
        try {
            Leave al = leaveServiceImpl.approveLeave(requestId);
            leaveServiceImpl.sendMailAndAddNotification(al, "Approved");
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomResponse("Fail", 500));
        }
    }

    @PostMapping("/admin/rejectLeave/{requestId}")
    public ResponseEntity<CustomResponse> rejectLeaveRequest(@PathVariable int requestId) {
        try {
            Leave al = leaveServiceImpl.rejectLeave(requestId);
            leaveServiceImpl.sendMailAndAddNotification(al, "Rejected");
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomResponse("Fail", 500));
        }
    }

    @GetMapping("/employee/getAllLeaveOfEmployee/{userId}")
    public ResponseEntity<Page<LeaveResponseDTO>> getAllLeaveOfEmployee(
            @PathVariable int userId,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(leaveServiceImpl.getAllLeaveOfEmployee(userId, pageNumber, pageSize));

    }

    @GetMapping("/admin/totalPendingLeave")
    public ResponseEntity<Integer> totalPendingLeave() {
        return ResponseEntity.ok(leaveServiceImpl.totalPendingLeave());
    }

    @GetMapping("/employee/totalPendingLeaveOfEmployee/{registerId}")
    public ResponseEntity<Integer> totalPendingLeaveOfEmployee(@PathVariable int registerId) {
        return ResponseEntity.ok(leaveServiceImpl.totalPendingLeaveOfEmployee(registerId));
    }

    @GetMapping("/employee/remainingLeave/{registerId}")
    public ResponseEntity<Integer> getRemainingLeaves(@PathVariable int registerId) {
        int remainingLeaves = leaveServiceImpl.getRemainingLeavesForQuarter(registerId);
        return ResponseEntity.ok(remainingLeaves);
    }

    @GetMapping("/employee/wfh-available/{registerId}")
    public ResponseEntity<Integer> getWfhLeavesAvailable(@PathVariable int registerId) {
        int wfhLeavesRemaining = leaveServiceImpl.getWFHLeavesRemaining(registerId);
        return ResponseEntity.ok(wfhLeavesRemaining);
    }

    @GetMapping("/employee/dashboard/{registerId}")
    public ResponseEntity<LeaveDashboardDTO> getLeaveDashboard(@PathVariable int registerId) {
        int remainingLeaves = leaveServiceImpl.getRemainingLeaves(registerId);
        int wfhLeavesRemaining = leaveServiceImpl.getWFHLeavesRemaining(registerId);
        int remainingRestrictedLeaves = leaveServiceImpl.getRemainingRestrictedLeaves(registerId);
        int currentQuarterLeavesUsed = leaveServiceImpl.getLeavesUsedInCurrentQuarter(registerId);
        int totalLeaves = leaveServiceImpl.calculateTotalLeaves(registerId);
        LeaveDashboardDTO dashboard = new LeaveDashboardDTO(remainingLeaves, wfhLeavesRemaining,
                remainingRestrictedLeaves, currentQuarterLeavesUsed, totalLeaves);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/adminEmployee/employees-on-leave")
    public ResponseEntity<List<LeaveDto>> getEmployeesOnLeaveForDate(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate recordDate;
        if (date == null) {
            recordDate = LocalDate.now();
        } else {

            recordDate = date;
        }
        return ResponseEntity.status(200).body(leaveServiceImpl.getEmployeesOnLeaveForDate(recordDate));
    }

    @GetMapping("/adminEmployee/employees-wfh")
    public ResponseEntity<List<LeaveDto>> getEmployeesOnWFHForDate(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate recordDate;
        if (date == null) {
            recordDate = LocalDate.now();
        } else {

            recordDate = date;
        }
        return ResponseEntity.status(200).body(leaveServiceImpl.getEmployeesOnWFHForDate(recordDate));
    }
}
