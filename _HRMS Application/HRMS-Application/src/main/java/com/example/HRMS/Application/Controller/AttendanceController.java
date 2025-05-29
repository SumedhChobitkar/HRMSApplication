package com.example.HRMS.Application.Controller;
import com.example.HRMS.Application.DTO.CheckTimeDto;
import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/attendance")
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody CheckTimeDto checkInDto) {
        try {
            Attendance savedAttendance = attendanceService.markAttendance(checkInDto);
            logger.info("Attendance marked for employeeId: {}", savedAttendance.getEmployeeId());
            return ResponseEntity.ok(savedAttendance);
        } catch (Exception e) {
            logger.error("Error marking attendance", e);
            return ResponseEntity.status(500).body("Error marking attendance");
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
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/getAttendanceByid/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        return attendanceService.getAttendanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @DeleteMapping("/DeleteAttendanceByid/{id}")
    public ResponseEntity<?> deleteAttendanceById(@PathVariable Long id) {
        try {
            attendanceService.deleteAttendanceById(id);
            logger.info("Deleted attendance with ID: {}", id);
            return ResponseEntity.ok("Attendance deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting attendance with ID: {}", id, e);
            return ResponseEntity.status(500).body("Failed to delete attendance");
        }
    }
}
