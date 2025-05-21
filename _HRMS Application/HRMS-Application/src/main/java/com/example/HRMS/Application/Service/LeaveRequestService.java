package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.LeaveRequest;

import java.util.List;
import java.util.Optional;

public interface LeaveRequestService {
    public LeaveRequest createLeaveRequest(LeaveRequest request);
    public LeaveRequest updateStatus(Long id, LeaveRequest.Status status);
    public Optional<LeaveRequest> getLeaveRequestById(Long id);
    public List<LeaveRequest> getAllLeaveRequests();
    public void deleteLeaveRequest(Long id);


}
