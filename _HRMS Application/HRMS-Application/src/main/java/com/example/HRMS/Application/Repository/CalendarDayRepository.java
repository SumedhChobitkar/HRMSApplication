package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.CalendarDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long> {

    Optional<CalendarDay> findByDate(LocalDate date);

    @Query("SELECT c FROM CalendarDay c WHERE MONTH(c.date) = :month AND YEAR(c.date) = :year")
    List<CalendarDay> findAllForMonth(int month, int year);
    List<CalendarDay> findByDateBetween(LocalDate from, LocalDate to);
}
