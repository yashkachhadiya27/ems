package com.backend.ems.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.CheckInOutDto;
import com.backend.ems.Entity.Attendance;
import com.backend.ems.Entity.CheckInOut;

@Repository
public interface CheckInOutRepository extends JpaRepository<CheckInOut, Integer> {
    Optional<CheckInOut> findFirstByAttendanceAndCheckOutTimeIsNull(Attendance attendance);

    List<CheckInOut> findByAttendance(Attendance attendance);

    @Query("SELECT new com.backend.ems.DTO.CheckInOutDto(c.checkInTime,c.checkOutTime) " +
            "FROM CheckInOut c WHERE c.attendance.attendanceDate = :date and c.attendance.register.id=:userId")
    List<CheckInOutDto> findByDate(@Param("userId") int userId, @Param("date") LocalDate date);
}
