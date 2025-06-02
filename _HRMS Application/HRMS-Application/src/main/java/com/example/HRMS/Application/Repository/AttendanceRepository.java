package com.example.HRMS.Application.Repository;
import com.example.HRMS.Application.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    /*@Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.date = :date")
Optional<Attendance> findAttendanceByEmployeeIdAndDate(@Param("employeeId") Long employeeId,
                                                       @Param("date") LocalDate date);*/
}

