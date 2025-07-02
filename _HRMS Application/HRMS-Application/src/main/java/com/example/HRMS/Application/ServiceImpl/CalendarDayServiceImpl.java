package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.*;
import com.example.HRMS.Application.Exception.ResourceNotFoundException;
import com.example.HRMS.Application.Repository.*;
import com.example.HRMS.Application.Service.CalendarDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
@Service
public class CalendarDayServiceImpl implements CalendarDayService {

    @Autowired
    private CalendarDayRepository calendarDayRepository;
    @Autowired
    private AttendanceRepository attendanceRepo;
    @Autowired
    private HolidayRepository holidayRepo;
    @Autowired
    private LeaveRequestRepository leaveRepo;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public CalendarDay setWorkingDay(LocalDate date, Boolean isWorkingDay, String remarks) {
        CalendarDay day = calendarDayRepository.findByDate(date).orElse(new CalendarDay());
        day.setDate(date);
        day.setIsWorkingDay(isWorkingDay);
        day.setRemarks(remarks);
        return calendarDayRepository.save(day);
    }

    @Override
    public CalendarDay getDay(LocalDate date) {
        return calendarDayRepository.findByDate(date)
                .orElseThrow(() -> new ResourceNotFoundException("No configuration found for date: " + date));
    }

    @Override
    public List<CalendarDay> getAllDays() {
        return calendarDayRepository.findAll();
    }

    @Override
    public List<CalendarDay> getDaysByMonth(int year, int month) {
        return calendarDayRepository.findAllForMonth(month, year);
    }

    public List<Map<String, Object>> getEmployeeCalendar(Long employeeId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate today = LocalDate.now();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        List<LeaveRequest> leaves = leaveRepo.findByEmployeeIdAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, end, start);

        List<Attendance> attendances = attendanceRepo.findByEmployeeIdAndDateBetween(employeeId, start, end);

        List<Holiday> holidays = holidayRepo.findByDateBetween(start, end);

        List<Map<String, Object>> result = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // Skip weekends
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            String status;
            if (date.isAfter(today)) {
                status = "";
            } else
             if (isHoliday(holidays, date)) {
                status = "H";
            } else
             if (isWeekend(date)) {
                 status = "O";
             } else if (isOnLeave(leaves, date)) {
                status = "A";
            } else if (isPresent(attendances, date)) {
                status = "P";
            } else {
                status = "A";
            }

            Map<String, Object> dayStatus = new HashMap<>();
            dayStatus.put("date", date);
            dayStatus.put("employeeId", employeeId);
            dayStatus.put("status", status);

            result.add(dayStatus);
        }

        return result;

    }
    private boolean isPresent(List<Attendance> attendances, LocalDate date) {
        return attendances.stream().anyMatch(a -> a.getDate().equals(date));
    }

    private boolean isOnLeave(List<LeaveRequest> leaves, LocalDate date) {
        return leaves.stream().anyMatch(lr ->
                !lr.getFromDate().isAfter(date) && !lr.getToDate().isBefore(date));
    }

    private boolean isHoliday(List<Holiday> holidays, LocalDate date) {
        return holidays.stream().anyMatch(h -> h.getDate().equals(date));
    }
    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

}
