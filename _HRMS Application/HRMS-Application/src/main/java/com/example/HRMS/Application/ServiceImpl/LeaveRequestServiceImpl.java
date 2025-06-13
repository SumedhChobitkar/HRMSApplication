package com.example.HRMS.Application.ServiceImpl;
import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.LeaveRequest;
import com.example.HRMS.Application.Entity.LeaveStatus;
import com.example.HRMS.Application.Entity.LeaveType;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.LeaveRequestRepository;
import com.example.HRMS.Application.Service.LeaveRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    @Autowired
    private LeaveRequestRepository repository;
    @Autowired
    EmployeeRepository employeeRepository;

   /* public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException {
        String fileType = file.getContentType();

        request.setStatus(LeaveStatus.PENDING);
        request.setLeaveType(LeaveType.CASUAL);
        request.setFileName(file.getOriginalFilename());
        request.setFileType(fileType);
        request.setData(file.getBytes());
        // Set default status

        return repository.save(request);
    }*/
    @Override
    public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException {
        if (request.getStatus() == null) {
            request.setStatus(LeaveStatus.PENDING); // Optional default
        }

        if (request.getLeaveType() == null) {
            request.setLeaveType(LeaveType.CASUAL); // Optional default
        }

        if (file != null) {
            request.setFileName(file.getOriginalFilename());
            request.setFileType(file.getContentType());
            request.setData(file.getBytes());
        }

        return repository.save(request);
    }

    @Override



    public List<LeaveRequest> getAllLeaveRequests() {
        return repository.findAll();
    }

    @Override
    public Optional<LeaveRequest> getLeaveRequestById(Long id) {
        return repository.findById(id);
    }
    @Override
    public LeaveRequest updateStatus(Long id, LeaveStatus status) {
        LeaveRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + id));
        request.setStatus(status);
        return repository.save(request);
    }
    @Override
    public String getApplyingToEmail() {
        String hrEmail = "hr@openfuturetechnologies.com";
        logger.debug("Returning hardcoded HR email: {}", hrEmail);
        return hrEmail;
    }
    @Override
    // 2. Return all employee emails
    public List<String> getAllEmployeeEmails() {
        logger.debug("Fetching all employee emails");
        List<Employee> employees = employeeRepository.findAll();

        List<String> emailList = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp.getEmail() != null) {
                emailList.add(emp.getEmail());
            }
        }

        logger.info("Fetched {} employee emails", emailList.size());
        return emailList;
    }

    @Override


   /* public Map<LeaveType, Long> getLeaveBalance(Long employeeId) {
        logger.info("Calculating leave balance for employeeId: {}", employeeId);

        List<LeaveRequest> approvedLeaves = repository
                .findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);

        Map<LeaveType, Long> leaveCountMap = new EnumMap<>(LeaveType.class);

        for (LeaveRequest leave : approvedLeaves) {
            LeaveType type = leave.getLeaveType();
            long days = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;

            leaveCountMap.put(type, leaveCountMap.getOrDefault(type, 0L) + days);
        }

        logger.info("Leave balance calculated for employeeId: {} is {}", employeeId, leaveCountMap);
        return leaveCountMap;
    }*/
    @Override
    public Map<String, Object> getLeaveBalance(Long employeeId) {
        logger.info("Calculating leave balance for employeeId: {}", employeeId);

        List<LeaveRequest> approvedLeaves = repository
                .findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);

        Map<LeaveType, Long> leaveCountMap = new EnumMap<>(LeaveType.class);
        long totalLeaveDays = 0;

        for (LeaveRequest leave : approvedLeaves) {
            LeaveType type = leave.getLeaveType();
            long days = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;

            leaveCountMap.put(type, leaveCountMap.getOrDefault(type, 0L) + days);
            totalLeaveDays += days;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalLeaveDays", totalLeaveDays);
        response.put("leaveBreakdown", leaveCountMap);

        logger.info("Leave balance calculated for employeeId: {} -> {}", employeeId, response);
        return response;
    }


    public void deleteLeaveRequest(Long id) {
        logger.info("Attempting to delete leave request with ID: {}", id);
        if (!repository.existsById(id)) {
            logger.warn("Leave request not found for deletion with ID: {}", id);
            throw new RuntimeException("Leave request not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Successfully deleted leave request with ID: {}", id);
    }

   /* public void saveAttachment(MultipartFile file, Long leaveId) throws IOException {
        String fileType = file.getContentType();

        if (!allowedTypes.contains(fileType)) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }

        LeaveRequest leaveRequest = repository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));

        LeaveRequest attachment = new LeaveRequest();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(fileType);
        attachment.setData(file.getBytes());
        repository.save(attachment);
    }
    *
    */


}

