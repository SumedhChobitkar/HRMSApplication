package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.CalendarDay;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CalendarDayService {
    CalendarDay setWorkingDay(LocalDate date, Boolean isWorkingDay, String remarks);
    CalendarDay getDay(LocalDate date);
    List<CalendarDay> getAllDays();
    List<CalendarDay> getDaysByMonth(int year, int month);
    public List<Map<String, Object>> getEmployeeCalendar(Long employeeId, int year, int month);
}
