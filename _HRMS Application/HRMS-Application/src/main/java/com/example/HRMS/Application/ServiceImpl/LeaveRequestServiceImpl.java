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


//    @Override
//    public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException {
//        if (request.getStatus() == null) {
//            request.setStatus(LeaveStatus.PENDING);
//        }
//
//        // Default leave type if not provided
//        if (request.getLeaveType() == null) {
//            request.setLeaveType(LeaveType.CASUAL);
//        }
//
//        // Document required for maternity leave
//        if (request.getLeaveType() == LeaveType.MATERNITY && file == null) {
//            throw new IllegalArgumentException("Document is required for maternity leave.");
//        }
//
//        // Store file if provided
//        if (file != null) {
//            request.setFileName(file.getOriginalFilename());
//            request.setFileType(file.getContentType());
//            request.setData(file.getBytes());
//        }
//
//        // Convert ccToList → comma-separated string
//        if (request.getCcToList() != null && !request.getCcToList().isEmpty()) {
//            request.setCcTo(String.join(",", request.getCcToList()));
//        }
//
//        // Validate leave limits before saving
//        validateLeaveLimit(request);
//
//        return repository.save(request);
//    }
@Override
public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException {
    if (request.getStatus() == null) {
        request.setStatus(LeaveStatus.PENDING);
    }

    // Default leave type if not provided
    if (request.getLeaveType() == null) {
        request.setLeaveType(LeaveType.CASUAL);
    }

    // Document required for maternity leave
    if (request.getLeaveType() == LeaveType.MATERNITY && file == null) {
        throw new IllegalArgumentException("Document is required for maternity leave.");
    }

    // Store file if provided
    if (file != null) {
        request.setFileName(file.getOriginalFilename());
        request.setFileType(file.getContentType());
        request.setData(file.getBytes());
    }

    // Convert ccToList → comma-separated string
    if (request.getCcToList() != null && !request.getCcToList().isEmpty()) {
        request.setCcTo(String.join(",", request.getCcToList()));
    }

    // Validate leave limits before saving
    validateLeaveLimit(request);

    return repository.save(request);
}

    private void validateLeaveLimit(LeaveRequest request) {
        LeaveType type = request.getLeaveType();

        // ✅ Skip validation for unpaid leave (unlimited days)
        if (type == LeaveType.UNPAID) {
            return;
        }

        Long employeeId = request.getEmployeeId();
        long requestedDays = ChronoUnit.DAYS.between(request.getFromDate(), request.getToDate()) + 1;

        // Fetch approved leaves for this employee
        List<LeaveRequest> approvedLeaves = repository.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);

        long usedDays = approvedLeaves.stream()
                .filter(lr -> lr.getLeaveType() == type)
                .mapToLong(lr -> ChronoUnit.DAYS.between(lr.getFromDate(), lr.getToDate()) + 1)
                .sum();

        long totalDays = usedDays + requestedDays;

        switch (type) {
            case SICK:
            case CASUAL:
            case PAID:
                if (totalDays > 3) {
                    throw new IllegalArgumentException(type + " leave exceeded. Max 3 days allowed. Used: "
                            + usedDays + ", Requested: " + requestedDays);
                }
                break;
            case MATERNITY:
                if (totalDays > 180) {
                    throw new IllegalArgumentException("Maternity leave exceeded. Max 180 days allowed. Used: "
                            + usedDays + ", Requested: " + requestedDays);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid leave type. Allowed: SICK, CASUAL, PAID, MATERNITY, UNPAID.");
        }
    }


    @Override
    public LeaveRequest updateStatus(Long id, LeaveStatus status) {
        return null;
    }

    @Override
    public LeaveRequest updateLeaveStatus(Long leaveId, LeaveStatus status) {
        LeaveRequest leave = repository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveId));

        // If status is null or not valid → default to PENDING
        if (status == null ||
                (status != LeaveStatus.APPROVED &&
                        status != LeaveStatus.REJECTED &&
                        status != LeaveStatus.CANCELLED)) {
            status = LeaveStatus.PENDING;
        }

        // Validate only if approving
        if (status == LeaveStatus.APPROVED) {
            validateLeaveLimit(leave);
        }

        // ✅ Only set status, don't modify remark
        leave.setStatus(status);
        return repository.save(leave);
    }
//    private void validateLeaveLimit(LeaveRequest request) {
//        Long employeeId = request.getEmployeeId();
//        LeaveType type = request.getLeaveType();
//        long requestedDays = ChronoUnit.DAYS.between(request.getFromDate(), request.getToDate()) + 1;
//
//        // Fetch approved leaves for this employee
//        List<LeaveRequest> approvedLeaves = repository.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);
//
//        long usedDays = approvedLeaves.stream()
//                .filter(lr -> lr.getLeaveType() == type)
//                .mapToLong(lr -> ChronoUnit.DAYS.between(lr.getFromDate(), lr.getToDate()) + 1)
//                .sum();
//
//        long totalDays = usedDays + requestedDays;
//
//        switch (type) {
//            case SICK:
//            case CASUAL:
//            case PAID:
//                if (totalDays > 3) {
//                    throw new IllegalArgumentException(type + " leave exceeded. Max 3 days allowed. Used: " + usedDays + ", Requested: " + requestedDays);
//                }
//                break;
//            case MATERNITY:
//                if (totalDays > 180) {
//                    throw new IllegalArgumentException("Maternity leave exceeded. Max 180 days allowed. Used: " + usedDays + ", Requested: " + requestedDays);
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid leave type. Allowed: SICK, CASUAL, PAID, MATERNITY.");
//        }
//    }


    @Override
    public Map<String, String> getLeaveBalance(Long employeeId) {
        List<LeaveRequest> approvedLeaves = repository.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);

        Map<String, String> balanceMessage = new HashMap<>();

        balanceMessage.put("SICK", getLeaveUsageMessage(approvedLeaves, LeaveType.SICK, 3));
        balanceMessage.put("CASUAL", getLeaveUsageMessage(approvedLeaves, LeaveType.CASUAL, 3));
        balanceMessage.put("PAID", getLeaveUsageMessage(approvedLeaves, LeaveType.PAID, 3));
        balanceMessage.put("MATERNITY", getLeaveUsageMessage(approvedLeaves, LeaveType.MATERNITY, 180));

        return balanceMessage;
    }

    private String getLeaveUsageMessage(List<LeaveRequest> leaves, LeaveType type, long totalAllowed) {
        long used = getUsedDays(leaves, type);
        long remaining = totalAllowed - used;
        return used + " used, " + remaining + " remaining";
    }

    private long getUsedDays(List<LeaveRequest> leaves, LeaveType type) {
        return leaves.stream()
                .filter(lr -> lr.getLeaveType() == type)
                .mapToLong(lr -> ChronoUnit.DAYS.between(lr.getFromDate(), lr.getToDate()) + 1)
                .sum();
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



    /*public LeaveRequest updateStatus(Long id, LeaveStatus status) {
        LeaveRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + id));
        request.setStatus(status);
        return repository.save(request);
    }*/

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
    public LeaveRequest addRemark(Long leaveId, String remark) {
        logger.info("Adding remark for leaveId: {}", leaveId);

        LeaveRequest leave = repository.findById(leaveId)
                .orElseThrow(() -> {
                    logger.warn("Leave request not found with ID: {}", leaveId);
                    return new RuntimeException("Leave request not found with ID: " + leaveId);
                });

        leave.setRemark(remark);
        return repository.save(leave);
    }
    @Override
    public String getRemarkByLeaveId(Long leaveId) {
        LeaveRequest leave = repository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveId));
        return leave.getRemark();
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

