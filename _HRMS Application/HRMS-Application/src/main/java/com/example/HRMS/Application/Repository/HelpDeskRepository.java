package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.HelpDesk;
import com.example.HRMS.Application.Entity.HelpDeskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HelpDeskRepository extends JpaRepository<HelpDesk, Long> {

    List<HelpDesk> findByHelpDeskStatus(HelpDeskStatus helpDeskStatus);

    List<HelpDesk> findByEmployeeId(Long employeeId);
    List<HelpDesk> findByEmployeeFirstNameAndEmployeeLastName(String firstName, String lastName);
}
