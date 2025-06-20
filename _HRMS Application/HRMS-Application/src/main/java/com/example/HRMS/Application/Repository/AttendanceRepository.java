package com.example.HRMS.Application.Repository;
import com.example.HRMS.Application.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    Optional<Object> findTodayByEmployeeId(Long employeeId);
    /*@Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.date = :date")
Optional<Attendance> findAttendanceByEmployeeIdAndDate(@Param("employeeId") Long employeeId,
                                                       @Param("date") LocalDate date);*/
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<Attendance> findByEmployeeIdAndMonth(Long employeeId, int year, int month);

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Attendance> findByEmployeeIdAndDateBetween(Long empId, LocalDate from, LocalDate to);

    Optional<Attendance> findByEmployeeIdAndDateAndClockOutIsNull(Long id, LocalDate localDate);
}

