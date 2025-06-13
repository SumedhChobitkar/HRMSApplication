package com.example.HRMS.Application.ServiceImpl;
import com.example.HRMS.Application.Controller.AttendanceController;
import com.example.HRMS.Application.DTO.CheckTimeDto;
import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Entity.AttendanceStatus;
import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Repository.AttendanceRepository;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;


    /* @Override
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
     }*/
    /*public Attendance markAttendance(CheckTimeDto checkInDto) {
        Long employeeId = checkInDto.getEmployeeId();
        LocalDate today = LocalDate.now();

        // Check if already marked
        Optional<Attendance> existingAttendance =
                attendanceRepository.findByEmployeeIdAndDate(employeeId, today);

        if (existingAttendance.isPresent()) {
            throw new RuntimeException("Already signed in today");
        }

        // Fetch employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Create attendance record
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee); // <-- important
        attendance.setLocation(checkInDto.getLocation());
        attendance.setDate(today);
        attendance.setClockIn(LocalTime.now());

        return attendanceRepository.save(attendance);
    }*/
    public Attendance markAttendance(CheckTimeDto checkInDto) {
        logger.info("Attempting to mark attendance for employeeId: {}", checkInDto.getEmployeeId());

        if (checkInDto.getEmployeeId() == null) {
            logger.warn("Employee ID is null in check-in DTO");
            throw new RuntimeException("Employee ID cannot be null");
        }

        Optional<Employee> employeeOpt = employeeRepository.findById(checkInDto.getEmployeeId());
        if (employeeOpt.isEmpty()) {
            logger.warn("Employee not found for ID: {}", checkInDto.getEmployeeId());
            throw new RuntimeException("Employee not found");
        }

        Employee employee = employeeOpt.get();
        LocalDate today = LocalDate.now();

        // Optional: prevent multiple sign-ins on same day
        Optional<Attendance> existing = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today);
        if (existing.isPresent()) {
            logger.warn("Attendance already marked for employeeId {} on {}", employee.getId(), today);
            throw new RuntimeException("Attendance already marked for today");
        }
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee); // Assuming Employee has a method to get ID
        attendance.setClockIn(LocalTime.now());
        attendance.setDate(today);
        attendance.setLocation(checkInDto.getLocation());
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setIssingin("TRUE");
        Attendance saved = attendanceRepository.save(attendance);

        logger.info("Attendance marked successfully with ID: {}", saved.getId());


        return saved;

}

 /*   public Attendance markSignOut(Long employeeId) {
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
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        logger.info("Service: Fetching all attendance records");
        List<Attendance> attendances = attendanceRepository.findAll();
        if (attendances.isEmpty()) {
            logger.warn("Service: No attendance records found");
            return ResponseEntity.status(404).body(null);
        } else {
            logger.info("Service: Found {} attendance records", attendances.size());
            return ResponseEntity.ok(attendances);
        }
    }*/
 public Attendance markSignOut(Long employeeId) {
     Attendance attendance = (Attendance) attendanceRepository.findTodayByEmployeeId(employeeId)
             .orElseThrow(() -> new RuntimeException("No sign-in record found"));

     if (attendance.getClockOut() != null) {
         throw new RuntimeException("Already signed out");
     }

     attendance.setClockOut(LocalTime.now());

     Duration duration = Duration.between(attendance.getClockIn(), attendance.getClockOut());
     String worked = String.format("%d hours %d minutes", duration.toHours(), duration.toMinutesPart());
     attendance.setWorkedHours(worked);

     log.info("Employee {} signed out at {}, worked: {}");

     attendance.setIssingin("FALSE");

     return attendanceRepository.save(attendance);
 }

    public ResponseEntity<List<Attendance>> getAllAttendance() {
        List<Attendance> attendanceList = attendanceRepository.findAll();

        if (attendanceList.isEmpty()) {
            log.warn("No attendance records found.");
            return ResponseEntity.noContent().build();
        }

        log.info("Fetched {} attendance records.");
        return ResponseEntity.ok(attendanceList);
    }


    public ResponseEntity<Attendance> getAttendanceById(Long id) {
        logger.info("Service: Fetching attendance with ID: {}", id);
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(id);
        if (attendanceOpt.isPresent()) {
            logger.info("Service: Attendance found for ID: {}", id);
            return ResponseEntity.ok(attendanceOpt.get());
        } else {
            logger.warn("Service: Attendance not found for ID: {}", id);
            return ResponseEntity.status(404).body(null);
        }
    }


    public ResponseEntity<?> updateAttendance(Long id, Attendance updatedAttendance) {
        logger.info("Service: Attempting to update attendance with ID: {}", id);
        Optional<Attendance> existingAttendanceOpt = attendanceRepository.findById(id);

        if (existingAttendanceOpt.isPresent()) {
            Attendance existingAttendance = existingAttendanceOpt.get();

            // Update basic fields
            existingAttendance.setDate(updatedAttendance.getDate());
            existingAttendance.setLocation(updatedAttendance.getLocation());
            existingAttendance.setClockIn(updatedAttendance.getClockIn());
            existingAttendance.setClockOut(updatedAttendance.getClockOut());

            // Handle employee update (if present)
            if (updatedAttendance.getEmployee() != null) {
                Long employeeId = updatedAttendance.getEmployee().getId(); // extract employeeId
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
                existingAttendance.setEmployee(employee);
            }

            attendanceRepository.save(existingAttendance);
            logger.info("Service: Successfully updated attendance with ID: {}", id);
            return ResponseEntity.ok("Attendance updated successfully");
        } else {
            logger.warn("Service: Attendance not found for ID: {}", id);
            return ResponseEntity.status(404).body("Attendance not found");
        }
    }

    public ResponseEntity<String> deleteAttendanceById(Long id) {
        logger.info("Service: Attempting to delete attendance with ID: {}", id);
        if (!attendanceRepository.existsById(id)) {
            logger.warn("Service: Attendance ID not found: {}", id);
            return ResponseEntity.status(404).body("Attendance not found");
        }

        try {
            attendanceRepository.deleteById(id);
            logger.info("Service: Successfully deleted attendance with ID: {}", id);
            return ResponseEntity.ok("Attendance deleted successfully");
        } catch (Exception e) {
            logger.error("Service: Error deleting attendance with ID: {}", id, e);
            return ResponseEntity.status(500).body("Failed to delete attendance");
        }
    }


}

