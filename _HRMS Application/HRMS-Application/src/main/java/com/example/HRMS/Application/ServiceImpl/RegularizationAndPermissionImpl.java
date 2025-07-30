package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.*;
import com.example.HRMS.Application.Repository.AttendanceRepository;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.RegularizationAndPermissionRepository;
import com.example.HRMS.Application.Service.RegularizationAndPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegularizationAndPermissionImpl implements RegularizationAndPermissionService {

    @Autowired
    private RegularizationAndPermissionRepository repository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public RegularizationAndPermission requestRegularization(Long employeeId, RegularizationAndPermission request) {
        validateRequest(employeeId, request);
        request.setRequestType(RequestType.REGULARIZATION);
        request.setApprovalStatus(ApprovalStatus.PENDING);
        return repository.save(request);
    }

    @Override
    public RegularizationAndPermission requestPermission(Long employeeId, RegularizationAndPermission request) {
        validateRequest(employeeId, request);
        request.setRequestType(RequestType.PERMISSION);
        request.setApprovalStatus(ApprovalStatus.PENDING);
        return repository.save(request);
    }

    @Override
    public RegularizationAndPermission approveRequest(Long requestId) {
        RegularizationAndPermission request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));
        request.setApprovalStatus(ApprovalStatus.APPROVED);
        return repository.save(request);
    }

    @Override
    public RegularizationAndPermission rejectRequest(Long requestId) {
        RegularizationAndPermission request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));
        request.setApprovalStatus(ApprovalStatus.REJECTED);
        return repository.save(request);
    }

    @Override
    public List<RegularizationAndPermission> getAllPendingRequests() {
        return repository.findByApprovalStatus(ApprovalStatus.PENDING);
    }

    private void validateRequest(Long employeeId, RegularizationAndPermission request) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (request.getDate() == null || request.getReason() == null || request.getReason().isBlank()) {
            throw new IllegalArgumentException("Date and Reason must not be null or empty");
        }
        if (request.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot request for a future date");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        request.setEmployee(employee);
    }
    @Override
    public List<RegularizationAndPermission> getPermissionsByEmployeeId(Long employeeId) {
        return repository.findByEmployeeIdAndRequestType(employeeId, RequestType.PERMISSION);
    }

    @Override
    public List<RegularizationAndPermission> getRegularizationsByEmployeeId(Long employeeId) {
        return repository.findByEmployeeIdAndRequestType(employeeId, RequestType.REGULARIZATION);
    }
    @Override
    public List<RegularizationAndPermission> getRequestByEmployeeId(Long employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    @Override
    public void deleteRequestById(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Request not found with ID: " + id);
        }
        repository.deleteById(id);
    }

}
