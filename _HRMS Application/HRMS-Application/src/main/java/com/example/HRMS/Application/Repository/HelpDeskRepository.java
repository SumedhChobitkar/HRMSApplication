package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.HelpDesk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpDeskRepository extends JpaRepository<HelpDesk, Long> {
}
