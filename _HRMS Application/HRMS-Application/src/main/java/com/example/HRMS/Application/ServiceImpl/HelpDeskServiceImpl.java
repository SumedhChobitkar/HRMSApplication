package com.example.HRMS.Application.ServiceImpl;


import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.HelpDesk;
import com.example.HRMS.Application.Entity.HelpDeskStatus;
import com.example.HRMS.Application.Exception.HelpDeskNotFoundException;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.HelpDeskRepository;
import com.example.HRMS.Application.Service.HelpDeskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class HelpDeskServiceImpl implements HelpDeskService {

    @Autowired
    private HelpDeskRepository repository;
    @Autowired
    private EmployeeRepository employeeRepository;

    private static final Logger logger = LoggerFactory.getLogger(HelpDeskServiceImpl.class);

    @Override
    public HelpDesk createHelpDesk(String helpDeskJson, MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            HelpDesk helpDesk = mapper.readValue(helpDeskJson, HelpDesk.class);

            if (file != null && !file.isEmpty()) {
                helpDesk.setAttachedFile(file.getBytes());
            }
            Long employeeId = helpDesk.getEmployee().getId();
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
            helpDesk.setEmployee(employee);
            helpDesk.setHelpDeskStatus(HelpDeskStatus.PENDING);
            helpDesk.setDate(LocalDate.now());
            return repository.save(helpDesk);
        } catch (Exception e) {
            throw new RuntimeException("Error creating helpdesk entry: " + e.getMessage());
        }
    }

    @Override
    public List<HelpDesk> getAllHelpDesks() {
        return repository.findAll();
    }
    @Override
    public HelpDesk getHelpDeskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HelpDesk ticket not found with ID: " + id));
    }
    @Override
    public void deleteHelpDeskById(Long id) {
        HelpDesk helpDesk = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));
        repository.delete(helpDesk);
    }

    @Override
    public List<HelpDesk> getHelpDeskByStatus(HelpDeskStatus helpDeskStatus) {
      return  repository.findByHelpDeskStatus(helpDeskStatus);
    }

    @Override
    public HelpDesk updateHelpDeskStatus(Long id,HelpDeskStatus helpDeskStatus) {
        HelpDesk helpDesk =repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("HelpDesk not found with id: " + id));

        helpDesk.setHelpDeskStatus(helpDeskStatus);
        helpDesk.setDate(LocalDate.now());
        return repository.save(helpDesk);
    }

    public List<HelpDesk> updateStatusByEmployeeId(Long empId, HelpDeskStatus helpDeskStatus) {
        List<HelpDesk> list = repository.findByEmployeeId(empId);
        if (list.isEmpty()) throw new EntityNotFoundException("No HelpDesk entries for employee ID " + empId);
        list.forEach(h -> h.setHelpDeskStatus(helpDeskStatus));

        return repository.saveAll(list);
    }

    public List<HelpDesk> updateStatusByEmployeeName(String firstName, String lastName, HelpDeskStatus helpDeskStatus) {
        List<HelpDesk> list = repository.findByEmployeeFirstNameAndEmployeeLastName(firstName, lastName);
        if (list.isEmpty()) throw new EntityNotFoundException("No HelpDesk entries for employee " + firstName + " " + lastName);
        list.forEach(h -> h.setHelpDeskStatus(helpDeskStatus));
        return repository.saveAll(list);
    }

    @Override
    public HelpDesk approveHelpDeskStatus(Long id, String remark) {
        try {
            logger.info("Attempting to approve HelpDesk with ID: {}", id);
            HelpDesk helpDesk = repository.findById(id)
                    .orElseThrow(() -> new HelpDeskNotFoundException("HelpDesk not found with id: " + id));

            helpDesk.setHelpDeskStatus(HelpDeskStatus.APPROVED);
            helpDesk.setRemark(remark);
            helpDesk.setDate(LocalDate.now());

            HelpDesk updated = repository.save(helpDesk);
            logger.info("HelpDesk ID {} approved successfully.", id);
            return updated;

        } catch (HelpDeskNotFoundException e) {
            logger.error("HelpDesk approval failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while approving HelpDesk: {}", e.getMessage(), e);
            throw new RuntimeException("Error while approving HelpDesk", e);
        }
    }

    @Override
    public HelpDesk rejectHelpDeskStatus(Long id, String remark) {
        try {
            logger.info("Attempting to reject HelpDesk with ID: {}", id);
            HelpDesk helpDesk = repository.findById(id)
                    .orElseThrow(() -> new HelpDeskNotFoundException("HelpDesk not found with id: " + id));

            helpDesk.setHelpDeskStatus(HelpDeskStatus.REJECTED);
            helpDesk.setRemark(remark);
            helpDesk.setDate(LocalDate.now());

            HelpDesk updated = repository.save(helpDesk);
            logger.info("HelpDesk ID {} rejected successfully.", id);
            return updated;

        } catch (HelpDeskNotFoundException e) {
            logger.error("HelpDesk rejection failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while rejecting HelpDesk: {}", e.getMessage(), e);
            throw new RuntimeException("Error while rejecting HelpDesk", e);
        }
    }

    @Override
    public List<HelpDesk> getHelpDesksByEmployeeId(Long employeeId) {
        try {
            logger.info("Fetching HelpDesk records for employee ID: {}", employeeId);
            List<HelpDesk> helpDesks = repository.findByEmployeeId(employeeId);

            if (helpDesks.isEmpty()) {
                throw new HelpDeskNotFoundException("No HelpDesk records found for employee ID: " + employeeId);
            }

            return helpDesks;
        } catch (HelpDeskNotFoundException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while fetching HelpDesk by employee ID: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching HelpDesk by employee ID", e);
        }
    }
}
