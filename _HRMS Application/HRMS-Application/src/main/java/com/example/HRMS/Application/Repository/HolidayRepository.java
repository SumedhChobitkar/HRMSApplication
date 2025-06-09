package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
}
