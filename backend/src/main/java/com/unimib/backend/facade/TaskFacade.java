package com.unimib.backend.facade;

import com.unimib.backend.DTO.TaskDTO;
import com.unimib.backend.POJO.Employee;
import com.unimib.backend.POJO.Task;
import com.unimib.backend.enums.TaskState;
import com.unimib.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskFacade {

    @Autowired
    private TaskService taskService;

    public Task createTask(TaskState initialState) {
        return taskService.createTask(initialState);
    }

    public Task createTask(TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    public Task createAndSaveTask(TaskDTO taskDTO) {
        return taskService.createAndSaveTask(taskDTO);
    }

    public Task saveTask(Task task) {
        return taskService.saveTask(task);
    }

    public void assignEmployeeToTask(Long taskId, Long employeeId) {
        taskService.assignEmployeeToTask(taskId, employeeId);
    }

    public void removeEmployeeToTask(Long taskId, Long employeeId) {
        taskService.removeEmployeeFromTask(taskId, employeeId);
    }

    public void changeTaskState(Long taskId, TaskState currentTaskState) {
        taskService.changeTaskState(taskId, currentTaskState);
    }

    public List<Task> getTasksByState(TaskState taskState) {
        return taskService.getTasksByState(taskState);
    }

    public List<Task> getTasksByEmployee(Employee employee) {
        return taskService.getTasksByEmployee(employee);
    }

    public List<Task> getUnsignedTasks() {
        return taskService.getUnassignedTasks();
    }

    public long countTasksByState(TaskState taskState) {
        return taskService.countTasksByState(taskState);
    }

    public List<Task> getComplexTasks(int employeeThreshold) {
        return taskService.getComplexTasks(employeeThreshold);
    }

    public Task getTaskById(Long taskId) {
        return taskService.getTaskById(taskId);
    }

    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    public void deleteTask(Long taskId) {
        taskService.deleteTask(taskId);
    }

    public boolean isEmployeeAssigned(Long taskId, Long employeeId) {
        return taskService.isEmployeeAssigned(taskId, employeeId);
    }

    /**
     * Resetta un task e rimuove tutti i dipendenti assegnati.
     */
    public void resetTask(Long taskId) {
        taskService.resetTask(taskId);
        List<Employee> assignedEmployees = taskService.getTaskById(taskId).getAssignedEmployees();
        assignedEmployees.forEach(employee -> taskService.removeEmployeeFromTask(taskId, employee.getWorkerId()));
    }

    public List<Task> findTasksByStateWithEmployee(TaskState taskState) {
        return taskService.getTasksByStateWithEmployees(taskState);
    }

    public Integer countEmployeeByTaskId(Long taskId) {
        return taskService.getEmployeeCountPerTask(taskId);
    }

    public List<Task> findTasksByStateAndCountEmployee(TaskState taskState, int employeeNumber) {
        return taskService.getTasksByStateAndEmployeeCount(taskState, employeeNumber);
    }

    public List<Task> findTasksByTeamId(Long idTeam) {
        return taskService.getTasksByTeam(idTeam);
    }

    public void setAssignedEmployees(@NonNull Long taskId, @NonNull List<Employee> employees) {
        taskService.setAssignedEmployees(taskId, employees);
    }

    public Task setTaskStartDate(Long taskId, LocalDateTime startDate) {
        return taskService.setTaskStartDate(taskId, startDate);
    }

    public Task setTaskEndDate(Long taskId, LocalDateTime endDate) {
        return taskService.setTaskEndDate(taskId, endDate);
    }
}