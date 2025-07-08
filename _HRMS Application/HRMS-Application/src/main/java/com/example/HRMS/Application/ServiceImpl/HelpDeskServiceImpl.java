package com.example.HRMS.Application.ServiceImpl;


import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.HelpDesk;
import com.example.HRMS.Application.Entity.HelpDeskStatus;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.HelpDeskRepository;
import com.example.HRMS.Application.Service.HelpDeskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class HelpDeskServiceImpl implements HelpDeskService {

    @Autowired
    private HelpDeskRepository repository;
    @Autowired
    private EmployeeRepository employeeRepository;

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

}
