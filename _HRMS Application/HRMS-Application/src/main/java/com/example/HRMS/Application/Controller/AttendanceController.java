package com.example.HRMS.Application.Controller;
import com.example.HRMS.Application.DTO.CheckTimeDto;
import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/attendance")
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

   /* @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody CheckTimeDto checkInDto) {
        try {
            Attendance savedAttendance = attendanceService.markAttendance(checkInDto);
            logger.info("Attendance marked for employeeId: {}", savedAttendance.getEmployeeId().getId());
            return ResponseEntity.ok(savedAttendance);
        } catch (Exception e) {
            logger.error("Error marking attendance", e);
            return ResponseEntity.status(500).body("Error marking attendance");
        }
    }*/

    /*@PostMapping("/signIn")
    public ResponseEntity<?> markAttendance(@RequestBody CheckTimeDto checkInDto) {
        try {
            Attendance attendance = attendanceService.markAttendance(checkInDto);
            return ResponseEntity.ok(attendance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }*/
    @PostMapping("/signIn")
    public ResponseEntity<?> markAttendance(@RequestBody CheckTimeDto checkInDto) {
        try {
            Attendance attendance = attendanceService.markAttendance(checkInDto);


            // Set or get the employee ID and attendance ID using CheckTimeDto
          //  checkInDto.setEmployeeId(attendance.getEmployee().getId());
            //checkInDto.setId(attendance.getId());
          //  checkInDto.setLocation(attendance.getLocation());
            Map<String,Object> response = new HashMap<>();
            response.put("employeeId", attendance.getEmployee().getId());
            response.put("attendanceId", attendance.getId());
            response.put("location", attendance.getLocation());
            response.put("clockIn", attendance.getClockIn());
            response.put("date", attendance.getDate());
            response.put("message", "Attendance marked successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/signOut")
    public ResponseEntity<?> signOut(@RequestBody CheckTimeDto checkOutDto) {
        try {
            Attendance updatedAttendance = attendanceService.markSignOut(checkOutDto.getEmployeeId());

            if (updatedAttendance.getClockOut() == null) {
                return ResponseEntity.status(500).body("Failed to sign out: clockOut was not updated.");
            }

            return ResponseEntity.ok(updatedAttendance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during sign-out");
        }
    }


    @GetMapping("/getAllAttendance")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }

    @GetMapping("/getAttendanceByid/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        return attendanceService.getAttendanceById(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    public ResponseEntity<?> updateAttendance(
            @PathVariable Long id,
            @RequestBody Attendance updatedAttendance
    ) {
        logger.info("Controller: Received request to update attendance with ID: {}", id);
        return attendanceService.updateAttendance(id, updatedAttendance);
    }

    @DeleteMapping("/DeleteAttendanceByid/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<String> deleteAttendanceById(@PathVariable Long id) {
        return attendanceService.deleteAttendanceById(id);
    }

}
