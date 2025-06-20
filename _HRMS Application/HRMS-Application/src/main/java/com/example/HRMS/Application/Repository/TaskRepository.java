package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
