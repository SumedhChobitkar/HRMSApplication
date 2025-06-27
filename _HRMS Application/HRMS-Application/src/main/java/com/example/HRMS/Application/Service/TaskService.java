package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.Task;
import com.example.HRMS.Application.Entity.TaskStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService {
    Task createTask(Task task, MultipartFile attachment, Long employeeId);

    Task updateTask(Long id, Task task);
    Task getTaskById(Long id);
    List<Task> getAllTasks();
    void deleteTask(Long id);

    public List<Task> getTaskByEmpId(Long employeeId);
    Task updateTaskStatus(Long taskId, TaskStatus status);
    void updateTaskStatusByEmployeeId(Long employeeId, TaskStatus status);

    Task updateTaskByEmployeeId(Long employeeId, Task task);
    List<String> getTaskStatusByEmployeeId(Long employeeId);


}
