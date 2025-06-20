package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.Task;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.TaskRepository;
import com.example.HRMS.Application.Service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    @Autowired
    private  TaskRepository taskRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(Task task, MultipartFile attachment, Long employeeId) {
        try {
            if (employeeId == null) {
                throw new IllegalArgumentException("Employee ID is required.");
            }

            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            if (attachment != null && !attachment.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + attachment.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String uploadPath = uploadDir + File.separator + fileName;
                File file = new File(uploadPath);
                attachment.transferTo(file);
                task.setAttachment(uploadPath);
            }

            task.setEmployee(employee);
            return taskRepository.save(task);

        } catch (Exception e) {
            throw new RuntimeException("Error creating task: " + e.getMessage());
        }
    }
    @Override
    public Task updateTask(Long id, Task task) {
        try {
            Task existing = taskRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task not found"));

            existing.setTaskName(task.getTaskName());
            existing.setAssignee(task.getAssignee());
            existing.setCheckList(task.getCheckList());
            existing.setPriority(task.getPriority());
            existing.setDueDate(task.getDueDate());
            existing.setTags(task.getTags());
            existing.setFollowers(task.getFollowers());
            existing.setDescription(task.getDescription());

            Task updated = taskRepository.save(existing);
            logger.info("Task updated: {}", updated.getId());
            return updated;
        } catch (Exception e) {
            logger.error("Update failed: {}", e.getMessage());
            throw new RuntimeException("Update failed");
        }
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
            logger.info("Task deleted: {}", id);
        } catch (Exception e) {
            logger.error("Delete failed: {}", e.getMessage());
            throw new RuntimeException("Delete failed");
        }
    }
}
