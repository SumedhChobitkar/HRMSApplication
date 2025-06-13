package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    @Query("SELECT h FROM Holiday h WHERE MONTH(h.date) = :month AND YEAR(h.date) = :year")
    List<Holiday> findByMonth(@Param("month") int month, @Param("year") int year);


    List<Holiday> findByDateBetween(LocalDate from, LocalDate to);

}
