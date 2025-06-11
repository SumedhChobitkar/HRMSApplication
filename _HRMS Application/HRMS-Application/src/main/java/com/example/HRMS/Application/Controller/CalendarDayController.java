package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.CalendarDay;
import com.example.HRMS.Application.Service.CalendarDayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar-days")
@Tag(name = "Employee Calendar", description = "View employee calendar attendance status")
public class CalendarDayController {

    @Autowired
    private CalendarDayService calendarDayService;

    @PostMapping("/set")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<CalendarDay> setWorkingDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Boolean isWorkingDay,
            @RequestParam(required = false) String remarks) {
        CalendarDay savedDay = calendarDayService.setWorkingDay(date, isWorkingDay, remarks);
        return ResponseEntity.ok(savedDay);
    }

    @GetMapping
    public ResponseEntity<List<CalendarDay>> getAllDays() {

        return ResponseEntity.ok(calendarDayService.getAllDays());
    }

    @GetMapping("/{date}")
    public ResponseEntity<CalendarDay> getDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(calendarDayService.getDay(date));
    }

    @GetMapping("/month")
    public ResponseEntity<List<CalendarDay>> getByMonth(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(calendarDayService.getDaysByMonth(year, month));
    }


    @GetMapping("/{employeeId}/{year}/{month}")
//    @Operation(
//            summary = "Get calendar view for an employee",
//            description = "Returns Present, Absent, Leave, Holiday, Working or Non-Working for each day",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
    public ResponseEntity<List<Map<String, Object>>> getCalendar(
            @PathVariable Long employeeId,
            @PathVariable int year,
            @PathVariable int month) {

        List<Map<String, Object>> calendar = calendarDayService.getEmployeeCalendar(employeeId, year, month);
        return ResponseEntity.ok(calendar);
    }
}
