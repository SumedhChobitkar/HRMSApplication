package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.LeaveRequest;
import com.example.HRMS.Application.Entity.LeaveStatus;
import com.example.HRMS.Application.Entity.LeaveType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LeaveRequestService {
 /* public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException;
  public LeaveRequest updateStatus(Long id, LeaveStatus status);
  public Optional<LeaveRequest> getLeaveRequestById(Long id);
  public List<LeaveRequest> getAllLeaveRequests();
  public void deleteLeaveRequest(Long id);
  //public String getApplyingToEmail();
  public List<String> getAllEmployeeEmails();
  public Map<String, Long> getLeaveBalance(Long employeeId);
  List<Map<String, Object>> getLeaveStatusesWithDatesByEmployeeId(Long employeeId);

  List<LeaveRequest> getLeaveByEmployeeName(String name);
  LeaveRequest updateLeave(Long leaveId, LeaveRequest updatedRequest, MultipartFile file) throws Exception;
  public void cancelLeaveRequest(Long id);
  public LeaveRequest updateLeaveStatus(Long leaveId, LeaveStatus status);
*/
 LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException;
 LeaveRequest updateStatus(Long id, LeaveStatus status);
 Optional<LeaveRequest> getLeaveRequestById(Long id);
 List<LeaveRequest> getAllLeaveRequests();
 void deleteLeaveRequest(Long id);
 List<String> getAllEmployeeEmails();
 public Map<String, String> getLeaveBalance(Long employeeId);
 List<Map<String, Object>> getLeaveStatusesWithDatesByEmployeeId(Long employeeId);
 List<LeaveRequest> getLeaveByEmployeeName(String name);
 LeaveRequest updateLeave(Long leaveId, LeaveRequest updatedRequest, MultipartFile file) throws Exception;
 void cancelLeaveRequest(Long id);
 public LeaveRequest updateLeaveStatus(Long leaveId, LeaveStatus status);


}
