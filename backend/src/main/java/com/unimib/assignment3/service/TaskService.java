package com.unimib.assignment3.service;

import com.unimib.assignment3.DTO.TaskDTO;
import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.constants.EmployeeConstants;
import com.unimib.assignment3.constants.TaskConstants;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.unimib.assignment3.constants.EmployeeConstants.EMPLOYEE_NOT_FOUND;
import static com.unimib.assignment3.constants.TaskConstants.TASK_NOT_FOUND;
import static com.unimib.assignment3.enums.TaskState.DONE;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeService employeeService;

    /**
     * Creates a new task with a given initial state.
     */
    @Transactional
    public Task createTask(TaskState initialState) {
        TaskState state = initialState != null ? initialState : TaskState.STARTED;
        Task task = new Task(state);

        if (state == TaskState.STARTED) {
            task.setStartDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)));
        } else if (state == DONE) {
            LocalDateTime now = LocalDateTime.now();
            task.setStartDate(now);
            task.setEndDate(now);
        }
        return task;
    }

    /**
     * Creates a new task from a TaskDTO.
     */
    @Transactional
    public Task createTask(TaskDTO taskDTO) {
        Task task = new Task(TaskState.TO_BE_STARTED);
        if (taskDTO.description() != null) {
            task.setDescription(taskDTO.description());
        }
        return task;
    }

    /**
     * Saves or updates a task.
     */
    @Transactional
    public Task saveTask(Task task) {
        if (task == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK);
        return taskRepository.saveAndFlush(task);
    }

    /**
     * Assigns an employee to a task.
     */
    @Transactional
    public void assignEmployeeToTask(Long taskId, Long employeeId) {
        if (employeeId == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE_ID);

        Task task = getTaskOrThrow(taskId);

        Employee employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));

        if (task.getTaskState() == DONE) {
            throw new IllegalStateException(TaskConstants.CANNOT_ASSIGN_DONE_TASK);
        }

        if (task.hasEmployee(employee)) {
            throw new IllegalStateException(EmployeeConstants.EMPLOYEE_ALREADY_ASSIGNED_TASK);
        }

        task.assignEmployee(employee);
    }

    /**
     * Removes an employee from a task.
     */
    @Transactional
    public void removeEmployeeFromTask(Long taskId, Long employeeId) {
        if (employeeId == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE_ID);

        Task task = getTaskOrThrow(taskId);

        Employee employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));

        if (!task.hasEmployee(employee)) {
            throw new IllegalStateException(EMPLOYEE_NOT_FOUND);
        }

        task.removeEmployee(employee);
    }

    /**
     * Changes a task's state.
     */
    @Transactional
    public Task changeTaskState(Long taskId, TaskState newState) {
        if (newState == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE);

        Task task = getTaskOrThrow(taskId);

        switch (newState) {
            case TO_BE_STARTED -> {
                task.setTaskState(TaskState.STARTED);
                task.setStartDate(LocalDateTime.now());
            }
            case STARTED -> {
                task.setTaskState(DONE);
                task.setEndDate(LocalDateTime.now());
            }
            case DONE -> throw new IllegalStateException(TaskConstants.TASK_ALREADY_FINISHED);
        }
        return task;
    }

    /**
     * Resets a task to TO_BE_STARTED.
     */
    @Transactional
    public void resetTask(Long taskId) {
        Task task = getTaskOrThrow(taskId);
        task.setTaskState(TaskState.TO_BE_STARTED);
        task.setStartDate(null);
        task.setEndDate(null);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByState(TaskState state) {
        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        return taskRepository.findByTaskState(state);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByEmployee(Employee employee) {
        if (employee == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE);
        if (employee.getWorkerId() == null) throw new IllegalArgumentException(EmployeeConstants.NULL_EMPLOYEE_ID);

        employeeService.findEmployeeById(employee.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));

        return taskRepository.findTasksByEmployee(employee);
    }

    @Transactional(readOnly = true)
    public List<Task> getUnassignedTasks() {
        return taskRepository.findTasksWithoutEmployee();
    }

    @Transactional(readOnly = true)
    public long countTasksByState(TaskState state) {
        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        return taskRepository.countByTaskState(state);
    }

    @Transactional(readOnly = true)
    public List<Task> getComplexTasks(int numEmployees) {
        if (numEmployees < 0) throw new IllegalArgumentException(TaskConstants.NEGATIVE_THRESHOLD);
        return taskRepository.findTasksWithMoreThanNEmployees(numEmployees);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return getTaskOrThrow(id);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public void deleteTask(Long id) {
        if (id == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_ID);
        if (!taskRepository.existsById(id)) throw new IllegalArgumentException(TASK_NOT_FOUND);
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isEmployeeAssigned(Long taskId, Long employeeId) {
        if (employeeId == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE_ID);

        Task task = getTaskOrThrow(taskId);
        Employee employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));

        return task.hasEmployee(employee);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByStateWithEmployees(TaskState state) {
        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        return taskRepository.findTasksByStateWithEmployees(state);
    }

    @Transactional(readOnly = true)
    public Integer getEmployeeCountPerTask(Long taskId) {
        return getTaskOrThrow(taskId).getAssignedEmployees().size();
    }

    @Transactional
    public void setAssignedEmployees(Long taskId, List<Employee> employees) {
        if (employees == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE);

        for (Employee employee : employees) {
            if (employee == null) throw new IllegalArgumentException(EmployeeConstants.NULL_EMPLOYEE);
        }

        Task task = getTaskOrThrow(taskId);
        task.setAssignedEmployees(employees);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByStateAndEmployeeCount(TaskState state, int numEmployees) {
        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        if (numEmployees < 0) throw new IllegalArgumentException(TaskConstants.NEGATIVE_THRESHOLD);

        return taskRepository.findTasksByStateAndEmployeesCount(state, numEmployees);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByTeam(Long idTeam) {
        if (idTeam == null) throw new IllegalArgumentException(TaskConstants.NULL_TEAM_ID);
        return taskRepository.findTasksByTeamId(idTeam);
    }

    @Transactional
    public Task setTaskStartDate(Long taskId, LocalDate startDate) {
        if (startDate == null) throw new IllegalArgumentException(TaskConstants.NULL_DATE);
        Task task = getTaskOrThrow(taskId);
        task.setStartDate(startDate.atStartOfDay());
        return task;
    }

    @Transactional
    public Task setTaskEndDate(Long taskId, LocalDate endDate) {
        if (endDate == null) throw new IllegalArgumentException(TaskConstants.NULL_DATE);
        Task task = getTaskOrThrow(taskId);
        task.setEndDate(endDate.atStartOfDay());
        return task;
    }

    private Task getTaskOrThrow(Long taskId) {
        if (taskId == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_ID);
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(TASK_NOT_FOUND));
    }
}