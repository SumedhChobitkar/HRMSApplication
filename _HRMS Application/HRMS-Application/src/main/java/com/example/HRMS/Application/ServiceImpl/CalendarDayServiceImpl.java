package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.*;
import com.example.HRMS.Application.Exception.ResourceNotFoundException;
import com.example.HRMS.Application.Repository.*;
import com.example.HRMS.Application.Service.CalendarDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            if (isHoliday(holidays, date)) {
                status = "Holiday";
            } else if (isOnLeave(leaves, date)) {
                status = "Absent (on Leave)";
            } else if (isPresent(attendances, date)) {
                status = "Present";
            } else {
                status = "Absent";
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

//    private boolean isPresent(List<Attendance> attendances, LocalDate date) {
//        return attendances.stream().anyMatch(a -> a.getDate().equals(date));
//    }
//
//    private boolean isOnLeave(List<LeaveRequest> leaves, LocalDate date) {
//        return leaves.stream().anyMatch(lr ->
//                !lr.getFromDate().isAfter(date) && !lr.getToDate().isBefore(date));
//    }

//    public List<Map<String, Object>> getEmployeeCalendar(Long employeeId, int year, int month) {
//        LocalDate start = LocalDate.of(year, month, 1);
//        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
//
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
//
//        // Approved leaves
//        List<LeaveRequest> leaves = leaveRepo.findByEmployeeIdAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
//                employeeId, LeaveStatus.APPROVED, end, start);
//
//        // Attendance records
//        List<Attendance> attendances = attendanceRepo.findByEmployeeIdAndDateBetween(employeeId, start, end);
//
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
//            // Skip Saturday and Sunday
//            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
//                continue;
//            }
//
//            String status;
//            if (isPresent(attendances, date)) {
//                status = "Present";
//            } else if (isOnLeave(leaves, date)) {
//                status = "Absent (on Leave)";
//            } else {
//                status = "Absent";
//            }
//
//            Map<String, Object> dayStatus = new HashMap<>();
//            dayStatus.put("employeeId", employeeId);
//            dayStatus.put("date", date);
//            dayStatus.put("status", status);
//
//
//            result.add(dayStatus);
//        }
//
//        return result;
//    }
//




//    @Override
//    public List<Map<String, Object>> getEmployeeCalendar(Long employeeId, int year, int month) {
//        YearMonth yearMonth = YearMonth.of(year, month);
//        List<Map<String, Object>> calendar = new ArrayList<>();
//
//        Map<LocalDate, Attendance> attendances = attendanceRepo.findByEmployeeIdAndMonth(employeeId, year, month)
//                .stream().collect(Collectors.toMap(Attendance::getDate, a -> a));
//
////        Map<LocalDate, Holiday> holidays = holidayRepo.findByMonth(month, year)
////                .stream().collect(Collectors.toMap(Holiday::getDate, h -> h));
////        Map<LocalDate, List<Holiday>> holidayMap = holidays.stream()
////                .collect(Collectors.groupingBy(Holiday::getDate));
//
//        Map<LocalDate, List<Holiday>> holidayMap = holidayRepo.findByMonth(month, year)
//                .stream().collect(Collectors.groupingBy(Holiday::getDate));
//
//
//
//
//        Map<LocalDate, CalendarDay> configDays = calendarDayRepository.findAllForMonth(month, year)
//                .stream().collect(Collectors.toMap(CalendarDay::getDate, c -> c));
//
//        Set<LocalDate> leaveDates = leaveRepo.findApprovedLeavesForMonth(employeeId, year, month).stream()
//                .flatMap(leave -> Stream.iterate(leave.getFromDate(), d -> !d.isAfter(leave.getToDate()), d -> d.plusDays(1)))
//                .collect(Collectors.toSet());
//
//        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
//            LocalDate date = yearMonth.atDay(day);
//            Map<String, Object> dayView = new HashMap<>();
//            dayView.put("date", date);
//
////            if (holidays.containsKey(date)) {
////                dayView.put("status", "Holiday");
////                dayView.put("name", holidays.get(date).getName());
////            }
//            if (holidayMap.containsKey(date)) {
//                dayView.put("status", "Holiday");
//                dayView.put("name", holidayMap.get(date).stream()
//                        .map(Holiday::getName)
//                        .collect(Collectors.joining(", ")));
//            }
//            else if (leaveDates.contains(date)) {
//                dayView.put("status", "Leave");
//            } else if (attendances.containsKey(date)) {
//                dayView.put("status", "Present");
//            } else {
//                DayOfWeek dayOfWeek = date.getDayOfWeek();
//                boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
//                if (configDays.containsKey(date)) {
//                    CalendarDay config = configDays.get(date);
//                    if (Boolean.TRUE.equals(config.getIsWorkingDay())) {
//                        dayView.put("status", "Working");
//                        dayView.put("remarks", config.getRemarks());
//                    } else {
//                        dayView.put("status", "Non-Working");
//                        dayView.put("remarks", config.getRemarks());
//                    }
//                } else {
//                    dayView.put("status", isWeekend ? "Non-Working" : "Absent");
//                }
//            }
//            calendar.add(dayView);
//        }
//
//        return calendar;
////    }
//public List<Map<String, Object>> getEmployeeCalendar(Long employeeId, int year, int month) {
//    LocalDate start = LocalDate.of(year, month, 1);
//    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
//
//    List<CalendarDay> calendarDays = calendarDayRepository.findByDateBetween(start, end);
//    List<Attendance> attendances = attendanceRepo.findByEmployeeIdAndDateBetween(employeeId, start, end);
//    List<LeaveRequest> leaves = leaveRepo.findByEmployeeIdAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
//            employeeId, LeaveStatus.APPROVED, end, start);
//    List<Holiday> holidays = holidayRepo.findByDateBetween(start, end);
//
//    List<Map<String, Object>> result = new ArrayList<>();
//
//    for (CalendarDay day : calendarDays) {
//        LocalDate date = day.getDate();
//        String status = "Absent";
//
//        if (!day.getIsWorkingDay()) {
//            status = "Non-Working";
//        } else if (containsDate(holidays, date)) {
//            status = "Holiday";
//        } else if (containsLeave(leaves, date)) {
//            status = "Leave";
//        } else if (containsAttendance(attendances, date)) {
//            status = "Present";
//        }
//
//        Map<String, Object> dayStatus = new HashMap<>();
//        dayStatus.put("date", date);
//        dayStatus.put("status", status);
//        dayStatus.put("remarks", day.getRemarks());
//
//        result.add(dayStatus);
//    }
//
//    return result;
//}
//
//    private boolean containsDate(List<Holiday> holidays, LocalDate date) {
//        return holidays.stream().anyMatch(h -> h.getDate().equals(date));
//    }
//
//    private boolean containsAttendance(List<Attendance> attendances, LocalDate date) {
//        return attendances.stream().anyMatch(a -> a.getDate().equals(date));
//    }
//
//    private boolean containsLeave(List<LeaveRequest> leaves, LocalDate date) {
//        return leaves.stream().anyMatch(lr ->
//                !lr.getFromDate().isAfter(date) && !lr.getToDate().isBefore(date));
//    }


}
