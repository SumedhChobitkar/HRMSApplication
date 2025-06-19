package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.Holiday;
import com.example.HRMS.Application.Service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/holidays")
@CrossOrigin("*")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;


    @Operation(summary = "Create a new holiday", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR','MANAGER')")
    @PostMapping
    public ResponseEntity<Holiday> createHoliday(@RequestBody Holiday holiday) {
        Holiday created = holidayService.createHoliday(holiday);
        return ResponseEntity.ok(created);
    }


    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    @PutMapping("/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable("id") Long id, @RequestBody Holiday holiday) {
        Holiday updated = holidayService.updateHoliday(id, holiday);
        return ResponseEntity.ok(updated);
    }
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    @DeleteMapping("id/{id}")
    public ResponseEntity<String> deleteHoliday(@PathVariable ("id") Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok("Holiday deleted successfully");
    }


    @GetMapping
    public ResponseEntity<List<Holiday>> getAllHolidays() {

        return ResponseEntity.ok(holidayService.getAllHolidays());
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(holidayService.getHolidayById(id));
    }
}
