package com.example.HRMS.Application.ServiceImpl;
import com.example.HRMS.Application.Controller.LeaveRequestController;
import com.example.HRMS.Application.Entity.LeaveRequest;
import com.example.HRMS.Application.Repository.LeaveRequestRepository;
import com.example.HRMS.Application.Service.LeaveRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    @Autowired
    private LeaveRequestRepository repository;

    @Override
        public LeaveRequest createLeaveRequest(LeaveRequest request) {
            request.setStatus(LeaveRequest.Status.PENDING); // Set default status
            return repository.save(request);
        }

        public List<LeaveRequest> getAllLeaveRequests() {
            return repository.findAll();
        }

        public Optional<LeaveRequest> getLeaveRequestById(Long id) {
            return repository.findById(id);
        }

        public LeaveRequest updateStatus(Long id, LeaveRequest.Status status) {
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
}

