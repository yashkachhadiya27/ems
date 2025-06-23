package com.backend.ems.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.AttendanceDto;
import com.backend.ems.DTO.AveragesDto;
import com.backend.ems.Entity.Attendance;
import com.backend.ems.Entity.Register;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
        Optional<Attendance> findByRegisterIdAndAttendanceDate(int userId, LocalDate date);

        @Query("SELECT new com.backend.ems.DTO.AttendanceDto(a.attendanceDate, a.totalInTime, a.totalBreakTime) " +
                        "FROM Attendance a WHERE a.register.id=:userId and EXTRACT(MONTH FROM a.attendanceDate) = :month AND EXTRACT(YEAR FROM a.attendanceDate) = :year")
        List<AttendanceDto> findByMonth(@Param("userId") int userId, @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT new com.backend.ems.DTO.AveragesDto(AVG(a.totalInTime), AVG(a.totalBreakTime), AVG(a.totalInTime - a.totalBreakTime)) "
                        +
                        "FROM Attendance a WHERE a.register.id=:userId and EXTRACT(MONTH FROM a.attendanceDate) = :month AND EXTRACT(YEAR FROM a.attendanceDate) = :year")
        AveragesDto calculateAveragesForMonth(@Param("userId") int userId, @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT r FROM Register r WHERE r.id NOT IN (SELECT a.register.id FROM Attendance a WHERE a.attendanceDate = :today and a.isPresent=true) and r.role<>'ADMIN'")
        List<Register> findEmployeesNotCheckedInForDate(@Param("today") LocalDate today);
}
