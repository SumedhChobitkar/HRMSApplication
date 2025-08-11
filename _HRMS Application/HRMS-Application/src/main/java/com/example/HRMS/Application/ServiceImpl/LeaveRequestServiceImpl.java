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

   /* @Override
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
    }*/
   @Override
   public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException {
       if (request.getStatus() == null) {
           request.setStatus(LeaveStatus.PENDING);
       }

       if (request.getLeaveType() == null) {
           request.setLeaveType(LeaveType.CASUAL);
       }

       // Validate file is mandatory for maternity leave
       if (request.getLeaveType() == LeaveType.MATERNITY && file == null) {
           throw new IllegalArgumentException("Document is required for maternity leave.");
       }

       if (file != null) {
           request.setFileName(file.getOriginalFilename());
           request.setFileType(file.getContentType());
           request.setData(file.getBytes());
       }
       if (request.getCcToList() != null && !request.getCcToList().isEmpty()) {
           String ccFormatted = String.join(",", request.getCcToList());
           request.setCcTo(ccFormatted);
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
    public List<Map<String, Object>> getLeaveStatusesWithDatesByEmployeeId(Long employeeId) {
        List<LeaveRequest> leaveRequests = repository.findByEmployeeId(employeeId);

        List<Map<String, Object>> responseList = new ArrayList<>();

        for (LeaveRequest leave : leaveRequests) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("fromDate", leave.getFromDate());
            data.put("toDate", leave.getToDate());
            data.put("status", leave.getStatus());
            data.put("EmployeeId",leave.getEmployeeId());
            responseList.add(data);
        }

        return responseList;
    }



    @Override
    public LeaveRequest updateStatus(Long id, LeaveStatus status) {
        LeaveRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + id));
        request.setStatus(status);
        return repository.save(request);
    }
//    @Override
//    public String getApplyingToEmail() {
//        String hrEmail = "hr@openfuturetechnologies.com";
//        logger.debug("Returning hardcoded HR email: {}", hrEmail);
//        return hrEmail;
//    }
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
  public Map<String, Object> getLeaveBalance(Long employeeId) {
      logger.info("Calculating leave balance for employeeId: {}", employeeId);

      final Map<LeaveType, Long> allowedLeaves = Map.of(
              LeaveType.SICK, 3L,
              LeaveType.CASUAL, 3L,
              LeaveType.PAID, 3L
              // Maternity and Unpaid handled separately
      );

      List<LeaveRequest> approvedLeaves = repository.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);

      Map<LeaveType, Long> usedLeaveMap = new EnumMap<>(LeaveType.class);
      long unpaidDays = 0;
      long maternityDays = 0;

      for (LeaveRequest leave : approvedLeaves) {
          LeaveType type = leave.getLeaveType();
          long days = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;

          switch (type) {
              case UNPAID:
                  unpaidDays += days;
                  break;
              case MATERNITY:
                  maternityDays += days;
                  break;
              default:
                  usedLeaveMap.put(type, usedLeaveMap.getOrDefault(type, 0L) + days);
                  break;
          }
      }

      Map<String, Object> balanceMap = new LinkedHashMap<>();
      for (LeaveType type : allowedLeaves.keySet()) {
          long used = usedLeaveMap.getOrDefault(type, 0L);
          long allowed = allowedLeaves.get(type);
          long remaining = Math.max(0, allowed - used);
          long excess = Math.max(0, used - allowed);

          balanceMap.put(type + "_used", used);
          balanceMap.put(type + "_remaining", remaining);
          balanceMap.put(type + "_excess_converted_to_unpaid", excess);
          unpaidDays += excess;
      }

      // Add maternity separately
      balanceMap.put("MATERNITY_used", maternityDays);
      balanceMap.put("MATERNITY_max_allowed", 180);

      // Add total unpaid
      balanceMap.put("unpaid_total_days", unpaidDays);

      logger.info("Leave balance response for employee {}: {}", employeeId, balanceMap);
      return balanceMap;
  }
    @Override
    public List<LeaveRequest> getLeaveByEmployeeName(String name) {
        return repository.findByEmployeeNameContainingIgnoreCase(name);
    }

    @Override
    public LeaveRequest updateLeave(Long leaveId, LeaveRequest updatedRequest, MultipartFile file) throws Exception {
        LeaveRequest existing = repository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveId));

        if (existing.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be edited.");
        }

        // Update relevant fields
        existing.setFromDate(updatedRequest.getFromDate());
        existing.setToDate(updatedRequest.getToDate());
        existing.setReason(updatedRequest.getReason());
        existing.setLeaveType(updatedRequest.getLeaveType());
        existing.setApplyingTo(updatedRequest.getApplyingTo());
        existing.setContactDetails(updatedRequest.getContactDetails());

        if (updatedRequest.getCcToList() != null) {
            existing.setCcTo(String.join(",", updatedRequest.getCcToList()));
        }

        if (file != null) {
            existing.setFileName(file.getOriginalFilename());
            existing.setFileType(file.getContentType());
            existing.setData(file.getBytes());
        }

        return repository.save(existing);
    }

    @Override
    public void deleteLeaveRequest(Long id) {
        logger.info("Attempting to delete leave request with ID: {}", id);
        if (!repository.existsById(id)) {
            logger.warn("Leave request not found for deletion with ID: {}", id);
            throw new RuntimeException("Leave request not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Successfully deleted leave request with ID: {}", id);
    }


    @Override
    public void cancelLeaveRequest(Long id) {
        logger.info("Attempting to cancel leave request with ID: {}", id);

        LeaveRequest leaveRequest = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Leave request not found with ID: {}", id);
                    return new RuntimeException("Leave request not found with ID: " + id);
                });

        if (leaveRequest.getStatus() == LeaveStatus.CANCELLED) {
            logger.warn("Leave request with ID {} is already cancelled.", id);
            throw new RuntimeException("Leave request is already cancelled.");
        }

        if (leaveRequest.getStatus() == LeaveStatus.PENDING) {
            leaveRequest.setStatus(LeaveStatus.CANCELLED);
            repository.save(leaveRequest);
            logger.info("Successfully cancelled leave request with ID: {}", id);
        } else {
            throw new RuntimeException("Leave request with ID " + id + " cannot be cancelled in its current state.");
        }
    }

}

