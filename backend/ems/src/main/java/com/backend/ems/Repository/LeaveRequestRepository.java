package com.backend.ems.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.LeaveResponseDTO;
import com.backend.ems.Entity.Leave;

@Repository
public interface LeaveRequestRepository extends JpaRepository<Leave, Integer> {
        String columns = "r.image,CONCAT(r.fname,' ',r.lname),r.email,r.department,l.id,l.leaveType,l.reason,l.leaveFromDate,l.leaveToDate,l.status";
        String searchColumns = "concat(r.fname,' ',r.lname,' ',r.department,' ',r.email,' ',l.leaveType)";
        String getAllLeave = "select new com.backend.ems.DTO.LeaveResponseDTO(" + columns
                        + ") from Leave l JOIN l.register r where l.status<>'Pending'";
        String getAllPendingLeave = "select new com.backend.ems.DTO.LeaveResponseDTO(" + columns
                        + ") from Leave l JOIN l.register r where l.status='Pending'";
        String getAllSearchedLeave = "select new com.backend.ems.DTO.LeaveResponseDTO(" + columns
                        + ") from Leave l JOIN l.register r where LOWER(" + searchColumns
                        + ") LIKE %?1% and l.status<>'Pending'";
        String getAllSearchedPendingLeave = "select new com.backend.ems.DTO.LeaveResponseDTO(" + columns
                        + ") from Leave l JOIN l.register r where LOWER(" + searchColumns
                        + ") LIKE %?1% AND l.status='Pending'";
        String getAllLeaveOfEmployee = "select new com.backend.ems.DTO.LeaveResponseDTO(" + columns
                        + ") from Leave l JOIN l.register r where r.id=:registerId";

        String pendingLeaveCount = "select count(*) from Leave l where l.status='Pending'";
        String pendingLeaveOfEmpCount = "select count(*) from Leave l where l.status='Pending' and l.register.id = :registerId";

        @Query(getAllLeave)
        public Page<LeaveResponseDTO> getAllLeave(Pageable pageable);

        @Query(getAllPendingLeave)
        public Page<LeaveResponseDTO> getAllPendingLeave(Pageable pageable);

        @Query(getAllSearchedLeave)
        public Page<LeaveResponseDTO> getAllSearchedLeave(String keyword, Pageable pageable);

        @Query(getAllSearchedPendingLeave)
        public Page<LeaveResponseDTO> getAllSearchedPendingLeave(String keyword, Pageable pageable);

        @Query(getAllLeaveOfEmployee)
        public Page<LeaveResponseDTO> findByRegisterId(int registerId, Pageable pageable);

        @Query(pendingLeaveCount)
        public int totalPendingLeave();

        @Query(pendingLeaveOfEmpCount)
        public int totalPendingLeaveOfEmployee(int registerId);

        @Query("SELECT COUNT(l) FROM Leave l WHERE l.register.id = :registerId AND " +
                        "((l.leaveFromDate BETWEEN :fromDate AND :toDate) OR (l.leaveToDate BETWEEN :fromDate AND :toDate))")
        int existsByRegisterIdAndLeaveDatesOverlap(int registerId, LocalDate fromDate, LocalDate toDate);

        @Query("SELECT COALESCE(SUM(l.regularLeavesTaken), 0) FROM Leave l WHERE l.register.id = :registerId AND " +
                        "((l.leaveFromDate BETWEEN :fromDate AND :toDate) OR (l.leaveToDate BETWEEN :fromDate AND :toDate) OR (l.appliedOn BETWEEN :fromDate AND :toDate))")
        long countLeavesInRange(@Param("registerId") int registerId, @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate);

        @Query("SELECT COALESCE(SUM(l.wfhLeavesTaken), 0) " +
                        "FROM Leave l WHERE l.register.id = :registerId AND " +
                        "l.leaveType = 'Work From Home' AND l.leaveFromDate >= :fromDate AND l.leaveToDate <= :toDate")
        long countWFHLeaveDaysInRange(int registerId, LocalDate fromDate, LocalDate toDate);

        @Query("SELECT COUNT(l) FROM Leave l WHERE l.register.id = :registerId " +
                        "AND l.leaveType = 'Work From Home' AND l.leaveFromDate >= :fromDate " +
                        "AND l.leaveToDate <= :toDate")
        long countWFHLeaveEntries(@Param("registerId") int registerId,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate);

        List<Leave> findLeaveByRegisterId(int registerId);

        @Query("SELECT COUNT(l) FROM Leave l WHERE l.register.id = :registerId AND l.leaveType = 'Restricted Holiday' AND YEAR(l.leaveFromDate) = YEAR(CURRENT_DATE)")
        long countRestrictedLeavesTaken(@Param("registerId") int registerId);

        @Query("SELECT l FROM Leave l WHERE l.register.id = :registerId AND l.leaveFromDate < :startOfQuarter")
        List<Leave> findLeaveByRegisterIdBeforeDate(@Param("registerId") int registerId,
                        @Param("startOfQuarter") LocalDate startOfQuarter);

        @Query("SELECT l FROM Leave l WHERE l.register.id = :registerId AND l.appliedOn >= :startDate AND l.appliedOn <= :endDate")
        List<Leave> findLeaveByRegisterIdInRange(@Param("registerId") int registerId,
                        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query("SELECT SUM(l.lossOfPayDays) FROM Leave l WHERE l.register.id = :registerId AND MONTH(l.leaveFromDate) = :month AND YEAR(l.leaveFromDate) = :year")
        int getLOPDaysForMonth(@Param("registerId") int registerId, @Param("month") int month, @Param("year") int year);

        @Query("SELECT l FROM Leave l WHERE :today BETWEEN l.leaveFromDate AND l.leaveToDate AND l.leaveType <> 'Work From Home'")
        List<Leave> findEmployeesOnLeaveForDate(@Param("today") LocalDate today);

        @Query("SELECT l FROM Leave l WHERE l.leaveType = 'Work From Home' AND :today BETWEEN l.leaveFromDate AND l.leaveToDate")
        List<Leave> findEmployeesOnWFHForDate(@Param("today") LocalDate today);

}
