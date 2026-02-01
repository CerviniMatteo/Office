package com.unimib.assignment3.service;


import com.unimib.assignment3.POJO.*;
import com.unimib.assignment3.constants.*;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

import static com.unimib.assignment3.constants.TaskConstants.TASK_NOT_FOUND;
import static com.unimib.assignment3.constants.EmployeeConstants.EMPLOYEE_NOT_FOUND;


/**
 * Service class for managing Task entities.
 * <p>
 * Provides operations for task creation, state management, employee assignment,
 * and advanced queries regarding task complexity and scheduling.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeService employeeService;

    /**
     * Creates a new task with an initial state and sets relevant dates.
     *
     * @param initialState the starting state of the task
     * @return the newly created task entity
     */
    @Transactional
    public Task createTask(TaskState initialState) {
        TaskState state = initialState != null ? initialState : TaskState.STARTED;
        Task task = new Task(state);

        if (state == TaskState.STARTED) {
            task.setStartDate(LocalDate.now());
        } else if (state == TaskState.DONE) {
            task.setStartDate(LocalDate.now());
            task.setEndDate(LocalDate.now());
        }
        return task;
    }


    /**
     * Saves or updates a task in the database.
     *
     * @param task the task entity to save
     * @return the persisted task entity
     */
    @Transactional
    public Task saveTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException(TaskConstants.NULL_TASK);
        }
        return taskRepository.saveAndFlush(task);
    }


    /**
     * Assigns an employee to a specific task.
     *
     * @param taskId     the ID of the task
     * @param employeeId the ID of the employee to assign
     * @throws IllegalArgumentException if the task or employee is not found
     * @throws IllegalStateException    if the task is already completed or the employee is already assigned
     */
    @Transactional
    public void assignEmployeeToTask(Long taskId, Long employeeId) {
        if (employeeId == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE_ID);

        Task task = getTaskOrThrow(taskId);

        Employee employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));

        if (task.getTaskState() == TaskState.DONE) {
            throw new IllegalStateException(TaskConstants.CANNOT_ASSIGN_DONE_TASK);

        }

        if (task.hasEmployee(employee)) {
            throw new IllegalStateException(EmployeeConstants.EMPLOYEE_ALREADY_ASSIGNED_TASK);
        }

        task.assignEmployee(employee);

        employeeService.saveEmployee(employee);
    }


    /**
     * Removes an employee from a specific task.
     *
     * @param taskId     the ID of the task
     * @param employeeId the ID of the employee to remove
     * @throws IllegalArgumentException if the task or employee is not found
     */
    @Transactional
    public void removeEmployeeFromTask(Long taskId, Long employeeId) {
        if (employeeId == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE_ID);

        Task task = getTaskOrThrow(taskId);

        Employee employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));
        task.removeEmployee(employee);


        employeeService.saveEmployee(employee);
    }


    /**
     * Transitions a task to a new state and updates dates accordingly.
     *
     * @param taskId   the ID of the task to update
     * @param newState the target state
     * @return the updated task entity
     * @throws IllegalStateException if the transition is logically invalid
     */
    @Transactional
    public Task changeTaskState(Long taskId, TaskState newState) {

        if (newState == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE);

        Task task = getTaskOrThrow(taskId);

        TaskState currentState = task.getTaskState();

        if (currentState == newState) return task;

        switch (currentState) {
            case TO_BE_STARTED:
                if (newState != TaskState.STARTED) {
                    throw new IllegalStateException(TaskConstants.ONLY_STARTED_FROM_TO_BE_STARTED);
                }
                task.setStartDate(LocalDate.now());
                break;

            case STARTED:
                if (newState != TaskState.DONE) {
                    throw new IllegalStateException(TaskConstants.ONLY_DONE_FROM_STARTED);
                }
                task.setEndDate(LocalDate.now());
                break;

            case DONE:
                throw new IllegalStateException(TaskConstants.TASK_ALREADY_FINISHED);
        }

        task.setTaskState(newState);
        return task;
    }


    /**
     * Resets a task to the 'TO_BE_STARTED' state and clears all associated dates.
     *
     * @param taskId the ID of the task to reset
     */
    @Transactional
    public void resetTask(Long taskId) {
        Task task = getTaskOrThrow(taskId);

        task.setTaskState(TaskState.TO_BE_STARTED);

        task.setEndDate(null);
        task.setStartDate(null);
    }

    /**
     * Retrieves tasks filtered by their current state.
     *
     * @param state the task state to filter by
     * @return a list of matching tasks
     */
    public List<Task> getTasksByState(TaskState state) {
        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        return taskRepository.findByTaskState(state);
    }


    /**
     * Retrieves all tasks assigned to a specific employee.
     *
     * @param employee the employee entity
     * @return a list of tasks assigned to the employee
     */
    public List<Task> getTasksByEmployee(Employee employee) {
        if (employee == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE);
        if (employee.getWorkerId() == null) throw new IllegalArgumentException(EmployeeConstants.NULL_EMPLOYEE_ID);

        employeeService.findEmployeeById(employee.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));
        return taskRepository.findTasksByEmployee(employee);
    }


    /**
     * Retrieves all tasks that have no employees assigned.
     *
     * @return a list of unassigned tasks
     */
    public List<Task> getUnassignedTasks() {
        return taskRepository.findTasksWithoutEmployee();
    }


    /**
     * Counts the total number of tasks in a specific state.
     *
     * @param state the task state to count
     * @return the count of tasks
     */
    public long countTasksByState(TaskState state) {
        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        return taskRepository.countByTaskState(state);
    }


    /**
     * Retrieves 'complex' tasks that have more than a specified number of employees assigned.
     *
     * @param numEmployees the employee threshold
     * @return a list of complex tasks
     */
    public List<Task> getComplexTasks(int numEmployees) {
        if (numEmployees < 0) throw new IllegalArgumentException(TaskConstants.NEGATIVE_THRESHOLD);
        return taskRepository.findTasksWithMoreThanNEmployees(numEmployees);
    }


    /**
     * Finds a task by its unique ID.
     *
     * @param id the task ID
     * @return an Optional containing the task if found
     */
    public Task getTaskById(Long id) {
        return getTaskOrThrow(id);
    }


    /**
     * Retrieves all tasks in the system.
     *
     * @return a list of all tasks
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }


    /**
     * Deletes a task from the system by ID.
     *
     * @param id the ID of the task to delete
     */
    @Transactional
    public void deleteTask(Long id) {
        if (id == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_ID);

        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException(TASK_NOT_FOUND);
        }
        taskRepository.deleteById(id);
    }


    /**
     * Checks if a specific employee is assigned to a specific task.
     *
     * @param taskId     the task ID
     * @param employeeId the employee ID
     * @return true if the employee is assigned, false otherwise
     */
    public boolean isEmployeeAssigned(Long taskId, Long employeeId) {
        if (employeeId == null) throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE_ID);

        Task task = getTaskOrThrow(taskId);

        Employee employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND));

        return task.hasEmployee(employee);
    }


    /**
     * Retrieves tasks in a specific state that have at least one employee assigned.
     *
     * @param state the task state
     * @return a list of matching tasks
     */
    public List<Task> getTasksByStateWithEmployees(TaskState state) {
        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        return taskRepository.findTasksByStateWithEmployees(state);
    }


    /**
     * Gets the total number of employees assigned to a specific task.
     *
     * @param taskId the task ID
     * @return the count of assigned employees
     */
    public Integer getEmployeeCountPerTask(Long taskId) {
        return getTaskOrThrow(taskId).getAssignedEmployees().size();
    }

    /**
     * Sets the list of employees assigned to a specific task.
     * Checks that the list and the employees within it are not null.
     *
     * @param taskId    the ID of the task
     * @param employees the list of employees to assign
     * @throws IllegalArgumentException if the list is null or contains null elements
     */
    @Transactional
    public void setAssignedEmployees(Long taskId, List<Employee> employees) {
        if (employees == null) {
            throw new IllegalArgumentException(TaskConstants.NULL_EMPLOYEE);
        }

        for (Employee employee : employees) {
            if (employee == null) {
                throw new IllegalArgumentException(EmployeeConstants.NULL_EMPLOYEE);
            }
        }

        Task task = getTaskOrThrow(taskId);
        task.setAssignedEmployees(employees);
        taskRepository.saveAndFlush(task);
    }

    /**
     * Retrieves tasks that match a specific state and an exact employee count.
     *
     * @param state        the task state
     * @param numEmployees the exact number of employees required
     * @return a list of matching tasks
     */
    public List<Task> getTasksByStateAndEmployeeCount(TaskState state, int numEmployees) {

        if (state == null) throw new IllegalArgumentException(TaskConstants.NULL_TASK_STATE_PARAM);
        if (numEmployees < 0) throw new IllegalArgumentException(TaskConstants.NEGATIVE_THRESHOLD);

        return taskRepository.findTasksByStateAndEmployeesCount(state, numEmployees);
    }


    /**
     * Retrieves all tasks belonging to a specific team.
     *
     * @param idTeam the team ID
     * @return a list of tasks for that team
     */
    public List<Task> getTasksByTeam(Long idTeam) {
        if (idTeam == null) throw new IllegalArgumentException(TaskConstants.NULL_TEAM_ID);
        return taskRepository.findTasksByTeamId(idTeam);
    }


    /**
     * Sets the start date for a specific task.
     *
     * @param taskId    the task ID
     * @param startDate the new start date
     * @return the updated task entity
     */
    @Transactional
    public Task setTaskStartDate(Long taskId, LocalDate startDate) {
        if (startDate == null) throw new IllegalArgumentException(TaskConstants.NULL_DATE);

        Task task = getTaskOrThrow(taskId);

        task.setStartDate(startDate);
        return task;
    }


    /**
     * Sets the end date for a specific task.
     *
     * @param taskId  the task ID
     * @param endDate the new end date
     * @return the updated task entity
     */
    @Transactional
    public Task setTaskEndDate(Long taskId, LocalDate endDate) {
        if (endDate == null) throw new IllegalArgumentException(TaskConstants.NULL_DATE);

        Task task = getTaskOrThrow(taskId);

        task.setEndDate(endDate);
        return task;
    }

    private Task getTaskOrThrow(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException(TaskConstants.NULL_TASK_ID);
        }
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(TASK_NOT_FOUND));
    }

}