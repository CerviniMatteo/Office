package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}