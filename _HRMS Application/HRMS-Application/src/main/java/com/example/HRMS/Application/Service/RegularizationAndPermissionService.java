package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Entity.RegularizationAndPermission;

import java.util.List;

public interface RegularizationAndPermissionService {

    RegularizationAndPermission requestRegularization(Long employeeId, RegularizationAndPermission request);

    RegularizationAndPermission requestPermission(Long employeeId, RegularizationAndPermission request);

    RegularizationAndPermission approveRequest(Long requestId);

    RegularizationAndPermission rejectRequest(Long requestId);

    List<RegularizationAndPermission> getAllPendingRequests();
    List<RegularizationAndPermission> getPermissionsByEmployeeId(Long employeeId);
    List<RegularizationAndPermission> getRegularizationsByEmployeeId(Long employeeId);
    void deleteRequestById(Long id);
}

