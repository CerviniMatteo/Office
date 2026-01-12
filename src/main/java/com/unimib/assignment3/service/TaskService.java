package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.EmployeeRepository;
import com.unimib.assignment3.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Creazione e salvataggio task tramite Repository
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

    @Transactional
    public Task saveTask(Task task) {
          return taskRepository.saveAndFlush(task);
    }


    // Assegnazione dipendente a task con validazione
    @Transactional
    public Task assignEmployeeToTask(Long taskId, Long employeeId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Employee> dipOpt = employeeRepository.findById(employeeId);

        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }
        if (dipOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found with: " + employeeId);
        }

        Task task = taskOpt.get();
        Employee employee = dipOpt.get();

        if (task.getTaskState() == TaskState.DONE) {
            throw new IllegalStateException("Isn't allowed to assign to employees tasks in" + TaskState.DONE +  "state");

        }

        if (task.hasEmployee(employee)) {
            throw new IllegalStateException("Employee already assigned to this task");
        }

        task.assignEmployee(employee);

        employeeRepository.saveAndFlush(employee);
        return taskRepository.saveAndFlush(task);
    }



    // Rimozione dipendente da task
    @Transactional
    public Task removeEmployeeFromTask(Long taskId, Long employeeId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Employee> dipOpt = employeeRepository.findById(employeeId);

        if (taskOpt.isEmpty() || dipOpt.isEmpty()) {
            throw new IllegalArgumentException("Task o Employee not found");
        }

        Task task = taskOpt.get();
        Employee employee = dipOpt.get();

        task.removeEmployee(employee);
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public Task changeTaskState(Long taskId, TaskState newState) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task non trovato con id: " + taskId));

        TaskState currentState = task.getTaskState();

        if (currentState == newState) return task;

        switch (currentState) {
            case TO_BE_STARTED:
                if (newState != TaskState.STARTED) {
                    throw new IllegalStateException("Da DAINIZIARE si può passare solo a INIZIATO.");
                }
                task.setStartDate(LocalDate.now());
                break;

            case STARTED:
                if (newState != TaskState.DONE) {
                    throw new IllegalStateException("Da INIZIATO si può passare solo a FINITO. Usa reset() per ricominciare.");
                }
                task.setEndDate(LocalDate.now());
                break;

            case DONE:
                throw new IllegalStateException("Il task è FINITO e non può più cambiare stato. Usa reset() per ricominciare.");
        }

        task.setTaskState(newState);
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public void resetTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task non trovato con id: " + taskId));

        task.setTaskState(TaskState.TO_BE_STARTED);

        task.setEndDate(null);
        task.setStartDate(null);

        taskRepository.saveAndFlush(task);
    }

    // Metodi di ricerca
    public List<Task> getTasksByState(TaskState state) {
        return taskRepository.findByTaskState(state);
    }

    public List<Task> getTasksByEmployee(Employee employee) {
        return taskRepository.findTasksByEmployee(employee);
    }

    public List<Task> getUnassignedTasks() {
        return taskRepository.findTasksWithoutEmployee();
    }

    public long countTasksByState(TaskState state) {
        return taskRepository.countByTaskState(state);
    }

    public List<Task> getComplexTasks(int numEmployees) {
        return taskRepository.findTasksWithMoreThanNEmployees(numEmployees);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public boolean isEmployeeAssigned(Long taskId, Long employeeId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Employee> dipOpt = employeeRepository.findById(employeeId);

        if (taskOpt.isEmpty() || dipOpt.isEmpty()) {
            return false;
        }

        return taskOpt.get().hasEmployee(dipOpt.get());
    }

    public List<Task> getTasksByStateWithEmployees(TaskState state) {
        return taskRepository.findTasksByStateWithEmployees(state);
    }

    public Integer getEmployeeCountPerTask(Long taskId) {
        return taskRepository.countEmployeesByTaskId(taskId);
    }

    public List<Task> getTasksByStateAndEmployeeCount(TaskState state, int numEmployees) {
        return taskRepository.findTasksByStateAndEmployeesCount(state, numEmployees);
    }

    public List<Task> getTasksByTeam(Long idTeam) {
        return taskRepository.findTasksByTeamId(idTeam);
    }

}