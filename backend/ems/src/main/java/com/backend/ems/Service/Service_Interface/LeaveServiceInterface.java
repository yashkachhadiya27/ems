package com.backend.ems.Service.Service_Interface;

import org.springframework.data.domain.Page;

import com.backend.ems.DTO.LeaveRequestDTO;
import com.backend.ems.DTO.LeaveResponseDTO;
import com.backend.ems.Entity.Leave;

public interface LeaveServiceInterface {
        public void submitLeaveRequest(LeaveRequestDTO lrd, String email, int id);

        public Page<LeaveResponseDTO> getAllLeave(Integer pageNumber, Integer pageSize, String sortBy,
                        String sortOrder);

        public Page<LeaveResponseDTO> getAllSearchedLeave(String keyword, Integer pageNumber, Integer pageSize,
                        String sortBy,
                        String sortOrder);

        public Page<LeaveResponseDTO> getAllPendingLeave(Integer pageNumber, Integer pageSize, String sortBy,
                        String sortOrder);

        public Page<LeaveResponseDTO> getAllSearchedPendingLeave(String keyword, Integer pageNumber, Integer pageSize,
                        String sortBy,
                        String sortOrder);

        public Leave approveLeave(int id);

        public Leave rejectLeave(int id);

        public Page<LeaveResponseDTO> getAllLeaveOfEmployee(int userId, Integer pageNumber, Integer pageSize);

        public int totalPendingLeave();

        public int totalPendingLeaveOfEmployee(int registerId);

        public void updateLeaveRequest(LeaveRequestDTO lrd, int leaveId);

        public void deleteLeaveById(int leaveId);
}
