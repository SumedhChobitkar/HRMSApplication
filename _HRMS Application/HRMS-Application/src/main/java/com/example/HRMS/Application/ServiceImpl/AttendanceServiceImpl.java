package com.example.HRMS.Application.ServiceImpl;
import com.example.HRMS.Application.DTO.CheckTimeDto;
import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Repository.AttendanceRepository;
import com.example.HRMS.Application.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;


    @Override
    public Attendance markAttendance(CheckTimeDto checkInDto) {
        Long employeeId = checkInDto.getEmployeeId();
        LocalDate today = LocalDate.now();

        Optional<Attendance> existingAttendanceOpt =
                attendanceRepository.findByEmployeeIdAndDate(employeeId, today);

        if (existingAttendanceOpt.isPresent()) {
            throw new RuntimeException("Already signed in today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setLocation(checkInDto.getLocation());
        attendance.setDate(today);
        attendance.setClockIn(LocalTime.now());

        return attendanceRepository.save(attendance);
    }

    public Attendance markSignOut(Long employeeId) {
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new RuntimeException("Sign-in record not found for today"));

        if (attendance.getClockOut() != null) {
            throw new RuntimeException("Already signed out today");
        }

        attendance.setClockOut(LocalTime.now());

        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    @Override
    public void deleteAttendanceById(Long id) {
        attendanceRepository.deleteById(id);
    }
}

