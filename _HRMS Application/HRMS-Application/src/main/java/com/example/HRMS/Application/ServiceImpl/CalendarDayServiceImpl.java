package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Entity.CalendarDay;
import com.example.HRMS.Application.Entity.Holiday;
import com.example.HRMS.Application.Exception.ResourceNotFoundException;
import com.example.HRMS.Application.Repository.AttendanceRepository;
import com.example.HRMS.Application.Repository.CalendarDayRepository;
import com.example.HRMS.Application.Repository.HolidayRepository;
import com.example.HRMS.Application.Repository.LeaveRequestRepository;
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


    @Override
    public List<Map<String, Object>> getEmployeeCalendar(Long employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<Map<String, Object>> calendar = new ArrayList<>();

        Map<LocalDate, Attendance> attendances = attendanceRepo.findByEmployeeIdAndMonth(employeeId, year, month)
                .stream().collect(Collectors.toMap(Attendance::getDate, a -> a));

//        Map<LocalDate, Holiday> holidays = holidayRepo.findByMonth(month, year)
//                .stream().collect(Collectors.toMap(Holiday::getDate, h -> h));
//        Map<LocalDate, List<Holiday>> holidayMap = holidays.stream()
//                .collect(Collectors.groupingBy(Holiday::getDate));

        Map<LocalDate, List<Holiday>> holidayMap = holidayRepo.findByMonth(month, year)
                .stream().collect(Collectors.groupingBy(Holiday::getDate));




        Map<LocalDate, CalendarDay> configDays = calendarDayRepository.findAllForMonth(month, year)
                .stream().collect(Collectors.toMap(CalendarDay::getDate, c -> c));

        Set<LocalDate> leaveDates = leaveRepo.findApprovedLeavesForMonth(employeeId, year, month).stream()
                .flatMap(leave -> Stream.iterate(leave.getFromDate(), d -> !d.isAfter(leave.getToDate()), d -> d.plusDays(1)))
                .collect(Collectors.toSet());

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            Map<String, Object> dayView = new HashMap<>();
            dayView.put("date", date);

//            if (holidays.containsKey(date)) {
//                dayView.put("status", "Holiday");
//                dayView.put("name", holidays.get(date).getName());
//            }
            if (holidayMap.containsKey(date)) {
                dayView.put("status", "Holiday");
                dayView.put("name", holidayMap.get(date).stream()
                        .map(Holiday::getName)
                        .collect(Collectors.joining(", ")));
            }
            else if (leaveDates.contains(date)) {
                dayView.put("status", "Leave");
            } else if (attendances.containsKey(date)) {
                dayView.put("status", "Present");
            } else {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
                if (configDays.containsKey(date)) {
                    CalendarDay config = configDays.get(date);
                    if (Boolean.TRUE.equals(config.getIsWorkingDay())) {
                        dayView.put("status", "Working");
                        dayView.put("remarks", config.getRemarks());
                    } else {
                        dayView.put("status", "Non-Working");
                        dayView.put("remarks", config.getRemarks());
                    }
                } else {
                    dayView.put("status", isWeekend ? "Non-Working" : "Absent");
                }
            }
            calendar.add(dayView);
        }

        return calendar;
    }
}
