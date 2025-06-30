package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.Task;
import com.example.HRMS.Application.Entity.TaskStatus;
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

            //  Store file as bytes instead of saving path
            if (attachment != null && !attachment.isEmpty()) {
                task.setAttachment(attachment.getBytes());
            }

            // Set default status if not provided
            if (task.getStatus() == null) {
                task.setStatus(TaskStatus.PENDING);
            }


            task.setEmployee(employee);
            task.setEmployeeFirstName(employee.getFirstName());
            task.setEmployeeLastName(employee.getLastName());

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
    public Task updateTaskByEmployeeId(Long employeeId, Task task) {
        try {
            List<Task> tasks = taskRepository.findByEmployeeId(employeeId);
            if (tasks.isEmpty()) {
                throw new RuntimeException("No task found for employee ID: " + employeeId);
            }

            Task existing = tasks.get(0); // Assuming one task per employee. If multiple, adjust logic accordingly.

            existing.setTaskName(task.getTaskName());
            existing.setAssignee(task.getAssignee());
            existing.setCheckList(task.getCheckList());
            existing.setPriority(task.getPriority());
            existing.setDueDate(task.getDueDate());
            existing.setTags(task.getTags());
            existing.setFollowers(task.getFollowers());
            existing.setDescription(task.getDescription());
            existing.setStatus(task.getStatus()); // if status is part of task now

            Task updated = taskRepository.save(existing);
            logger.info("Task updated for employee ID {}: Task ID {}", employeeId, updated.getId());
            return updated;

        } catch (Exception e) {
            logger.error("Failed to update task for employee ID {}: {}", employeeId, e.getMessage());
            throw new RuntimeException("Update failed");
        }
    }

    @Override
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Override
    public void updateTaskStatusByEmployeeId(Long employeeId, TaskStatus status) {
        List<Task> tasks = taskRepository.findByEmployeeId(employeeId);
        if (tasks.isEmpty()) {
            throw new RuntimeException("No tasks found for employee with ID: " + employeeId);
        }

        for (Task task : tasks) {
            task.setStatus(status);
        }

        taskRepository.saveAll(tasks);
    }


    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    @Override
    public List<Task> getTaskByEmpId(Long employeeId) {

        return  taskRepository.findByEmployeeId(employeeId);


    }
    @Override
    public List<String> getTaskStatusByEmployeeId(Long employeeId) {
        List<Task> tasks = taskRepository.findByEmployeeId(employeeId);
        if (tasks.isEmpty()) {
            throw new RuntimeException("No tasks found for employee ID: " + employeeId);
        }

        return tasks.stream()
                .map(task -> "Task ID: " + task.getId() + " - Status: " + task.getStatus())
                .toList();
    }
    @Override
    public List<Task> getTasksByEmployeeName(String firstName, String lastName) {
        List<Task> tasks = taskRepository.findByEmployee_FirstNameAndEmployee_LastName(firstName, lastName);
        if (tasks.isEmpty()) {
            throw new RuntimeException("No tasks found for employee: " + firstName + " " + lastName);
        }
        return tasks;
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
