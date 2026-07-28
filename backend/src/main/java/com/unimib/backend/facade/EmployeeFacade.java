package com.unimib.backend.facade;

import com.unimib.backend.POJO.Employee;
import com.unimib.backend.POJO.Task;
import com.unimib.backend.enums.TaskState;
import com.unimib.backend.enums.WorkerRole;
import com.unimib.backend.service.EmployeeService;
import com.unimib.backend.service.UserChatMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeFacade {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UserChatMappingService userChatMappingService;

    public Employee createEmployee(@NonNull String name, @NonNull String surname) {
        return employeeService.createEmployee(name, surname);
    }

    public Employee createEmployee(@NonNull String name, @NonNull String surname, @NonNull String image) {
        return employeeService.createEmployee(name, surname, image);
    }

    public Employee createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull WorkerRole workerRole) {
        return employeeService.createEmployee(name, surname, monthlySalary, workerRole);
    }

    public Employee saveEmployee(@NonNull Employee employee) {
        return employeeService.saveEmployee(employee);
    }

    public List<Employee> saveAllEmployees(@NonNull List<Employee> employees) {
        return employeeService.saveAllEmployees(employees);
    }

    /**
     * Registra un nuovo dipendente: crea, salva, e crea/salva la chat associata.
     */
    @Transactional
    public Employee registerEmployee(@NonNull String name, @NonNull String surname, String encodedImage) {
        Employee employee = employeeService.createEmployee(name, surname, encodedImage);
        employee = employeeService.saveEmployee(employee);
        var chatMapping = userChatMappingService.createChat(employee.getWorkerId());
        userChatMappingService.saveChat(chatMapping);
        return employee;
    }

    public Optional<Employee> findEmployeeById(@NonNull Long employeeId) {
        return employeeService.findEmployeeById(employeeId);
    }

    public Optional<Long> findEmployeeIdByEmail(@NonNull String email) {
        return employeeService.findEmployeeIdByEmail(email);
    }

    public List<Employee> findAllEmployees() {
        return employeeService.findAllEmployees();
    }

    public List<Task> findTasksByWorkerAndTaskState(@NonNull Long workerId, @NonNull TaskState taskState) {
        return employeeService.findTasksByEmployeeAndTaskState(workerId, taskState);
    }

    public List<Task> findTasksByEmployeeByTaskStateByStartDate(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate startDate) {
        return employeeService.findTasksByEmployeeByTaskStateByStartDate(employeeId, taskState, startDate);
    }

    public List<Task> findTasksByEmployeeByTaskStateByEndDate(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate endDate) {
        return employeeService.findTasksByEmployeeByTaskStateByEndDate(employeeId, taskState, endDate);
    }

    public List<Task> findTasksByEmployeeByTaskStateByStartDateBetweenAndEndDateBetween(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        return employeeService.findTasksByEmployeeByTaskStateBetweenStartDateAndEndDate(employeeId, taskState, startDate, endDate);
    }

    public List<Task> findTasksByEmployeeByTaskStateOrderByStartDateDesc(@NonNull Long employeeId, @NonNull TaskState taskState) {
        return employeeService.findTasksByEmployeeByTaskStateOrderByStartDateDesc(employeeId, taskState);
    }

    public List<Task> findTasksByEmployeeByTaskStateOrderByEndDateDesc(@NonNull Long employeeId, @NonNull TaskState taskState) {
        return employeeService.findTasksByEmployeeByTaskStateOrderByEndDateDesc(employeeId, taskState);
    }
}