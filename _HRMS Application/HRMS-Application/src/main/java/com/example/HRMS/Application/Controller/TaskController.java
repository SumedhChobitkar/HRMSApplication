package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.Task;
import com.example.HRMS.Application.Entity.TaskStatus;
import com.example.HRMS.Application.Service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

//    @PostMapping
//    public ResponseEntity<?> createTask(
//            @RequestParam("task") String taskJson,
//            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
//            @RequestParam("employeeId") Long employeeId) {
//
//        try {
//            // Parse JSON manually
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//
//            Task task = objectMapper.readValue(taskJson, Task.class);
//
//            Task saved = taskService.createTask(task, attachment, employeeId);
//            return ResponseEntity.ok("Task created with ID: " + saved.getId());
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

    @PostMapping
    public ResponseEntity<?> createTask(
            @RequestParam("task") String taskJson,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            @RequestParam("employeeId") Long employeeId) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            Task task = objectMapper.readValue(taskJson, Task.class);
            Task saved = taskService.createTask(task, attachment, employeeId);


            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task created successfully");
            response.put("taskId", saved.getId());
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Server error: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Task not found");
        }
    }
    @GetMapping("employee/{employeeId}")
    public ResponseEntity<?> getTaskByEmpId(@PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(taskService.getTaskByEmpId(employeeId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Task not found");
        }
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    @GetMapping("/employee/{employeeId}/statuses")
    public ResponseEntity<?> getTaskStatusesByEmployeeId(@PathVariable Long employeeId) {
        try {
            List<String> statuses = taskService.getTaskStatusByEmployeeId(employeeId);
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);

        if (task.getAttachment() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("task_attachment").build());

        return new ResponseEntity<>(task.getAttachment(), headers, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody @Valid Task task) {
        try {
            Task updated = taskService.updateTask(id, task);
            return ResponseEntity.ok("Task updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating task: " + e.getMessage());
        }
    }
    @PutMapping("/employee/{employeeId}")
    public ResponseEntity<?> updateTaskByEmployeeId(
            @PathVariable Long employeeId,
            @RequestBody @Valid Task task) {
        try {
            Task updated = taskService.updateTaskByEmployeeId(employeeId, task);
            return ResponseEntity.ok("Task updated successfully for employee ID: " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating task: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam("status") TaskStatus status) {
        try {
            Task updated = taskService.updateTaskStatus(id, status);
            return ResponseEntity.ok("Task status updated to: " + updated.getStatus());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating status: " + e.getMessage());
        }
    }

    @PutMapping("/employee/{employeeId}/status")
    public ResponseEntity<?> updateTaskStatusByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam("status") TaskStatus status) {
        try {
            taskService.updateTaskStatusByEmployeeId(employeeId, status);
            return ResponseEntity.ok("All tasks for employee ID " + employeeId + " updated to status: " + status);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting task: " + e.getMessage());
        }
    }
}
