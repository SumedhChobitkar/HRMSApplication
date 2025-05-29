package com.example.HRMS.Application.ServiceImpl;
import com.example.HRMS.Application.Entity.LeaveRequest;
import com.example.HRMS.Application.Entity.LeaveStatus;
import com.example.HRMS.Application.Entity.LeaveType;
import com.example.HRMS.Application.Repository.LeaveRequestRepository;
import com.example.HRMS.Application.Service.LeaveRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    @Autowired
    private LeaveRequestRepository repository;

    @Override
    public LeaveRequest createLeaveRequest(LeaveRequest request, MultipartFile file) throws IOException {
        String fileType = file.getContentType();

        request.setStatus(LeaveStatus.PENDING);
        request.setLeaveType(LeaveType.CASUAL);
        request.setFileName(file.getOriginalFilename());
        request.setFileType(fileType);
        request.setData(file.getBytes());
        // Set default status

        return repository.save(request);
    }
    public List<LeaveRequest> getAllLeaveRequests() {
        return repository.findAll();
    }

    public Optional<LeaveRequest> getLeaveRequestById(Long id) {
        return repository.findById(id);
    }

    public LeaveRequest updateStatus(Long id, LeaveStatus status) {
        LeaveRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + id));
        request.setStatus(status);
        return repository.save(request);
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

