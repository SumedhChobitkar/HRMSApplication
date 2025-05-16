package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.DTO.CheckTimeDto;
import com.example.HRMS.Application.Entity.Attendance;

import java.util.Optional;
import java.util.List;

public interface AttendanceService {

        public Attendance markAttendance(CheckTimeDto checkInDto);

        public Attendance markSignOut(Long employeeId);

        List<Attendance> getAllAttendance();

        Optional<Attendance> getAttendanceById(Long id);

        void deleteAttendanceById(Long id);
    }
