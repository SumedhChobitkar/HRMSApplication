package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.ApprovalStatus;
import com.example.HRMS.Application.Entity.RegularizationAndPermission;
import com.example.HRMS.Application.Entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegularizationAndPermissionRepository extends JpaRepository<RegularizationAndPermission, Long> {
    List<RegularizationAndPermission> findByApprovalStatus(ApprovalStatus approvalStatus);

    List<RegularizationAndPermission> findByEmployeeIdAndRequestType(Long employeeId, RequestType requestType);
}

