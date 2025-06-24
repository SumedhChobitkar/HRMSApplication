package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.DTO.CheckTimeDto;
import com.example.HRMS.Application.Entity.Attendance;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AttendanceService {

       public Attendance markAttendance(CheckTimeDto checkInDto);

        // Method to mark attendance for an employee
        // public Attendance markAttendance(Attendance attendance);
        // Method to mark sign-out for an employee
       //public Attendance markAttendance(Attendance attendance);
        public Attendance markSignOut(Long employeeId);
        public ResponseEntity<List<Attendance>> getAllAttendance();
        public ResponseEntity<Attendance> getAttendanceById(Long id);
        public ResponseEntity<?> updateAttendance(Long id, Attendance updatedAttendance);
        public ResponseEntity<String> deleteAttendanceById(Long id);
    ResponseEntity<List<Attendance>> getAttendanceByEmployeeId(Long employeeId);

    }
