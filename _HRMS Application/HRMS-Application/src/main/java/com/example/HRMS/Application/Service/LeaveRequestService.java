package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.LeaveRequest;
import com.example.HRMS.Application.Entity.LeaveStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LeaveRequestService {
    public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException;
    public LeaveRequest updateStatus(Long id, LeaveStatus status);
    public Optional<LeaveRequest> getLeaveRequestById(Long id);
    public List<LeaveRequest> getAllLeaveRequests();
    public void deleteLeaveRequest(Long id);
    public String getApplyingToEmail();
    public List<String> getAllEmployeeEmails();
    public Map<String, Object> getLeaveBalance(Long employeeId);

}
