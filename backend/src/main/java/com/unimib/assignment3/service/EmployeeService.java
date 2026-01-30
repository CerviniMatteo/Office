package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.*;
import com.unimib.assignment3.enums.*;
import com.unimib.assignment3.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.unimib.assignment3.POJO.Worker.generateEmail;
import static com.unimib.assignment3.constants.CommonConstants.*;
import static com.unimib.assignment3.constants.EmployeeConstants.*;
import static com.unimib.assignment3.constants.TaskConstants.NULL_DATE;
import static com.unimib.assignment3.constants.TaskConstants.NULL_TASK_STATE;

/**
 * Service class for managing Dipendente (employee) entities.
 * <p>
 * Provides CRUD operations, salary and role updates, task queries, and manager-authorized actions.
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Save a single employee to the database and handles email uniqueness.
     *
     * @param employee the employee to save
     * @throws IllegalArgumentException if email is not unique and if employee is null
     * @return the saved employee
     */
    public Employee saveEmployee(@NonNull Employee employee) {
        checkUniqueEmail(employee);
        try {
            return employeeRepository.saveAndFlush(employee);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(EMAIL_HAVE_TO_BE_UNIQUE);
        }
    }

    /**
     * Save multiple employees to the database in bulk.
     *
     * @param employees list of employees
     * @throws IllegalArgumentException if the employees list is null
     * @return list of saved employees
     */
    public List<Employee> saveAllEmployees(@NonNull List<Employee> employees) {
        assertNotNull(employees, NULL_EMPLOYEES);

        List<Employee> savedEmployees = new ArrayList<>();

        for (Employee employee : employees) {
            checkUniqueEmail(employee);

            try {
                Employee saved = employeeRepository.saveAndFlush(employee);
                savedEmployees.add(saved);
            } catch (DataIntegrityViolationException e) {
                throw new IllegalArgumentException(EMAIL_HAVE_TO_BE_UNIQUE);
            }
        }

        return savedEmployees;
    }


    /**
     * Ensure the employee's email is unique by appending a counter if necessary.
     * @param employee the employee whose email needs to be checked
     * @throws IllegalArgumentException if the employee is null
     */
    private void checkUniqueEmail(Employee employee) {
        assertNotNull(employee, NULL_EMPLOYEE);
        int emailCounter = employeeRepository.countEmailsStartingWithEmailPrefix(employee.getName());
        if(emailCounter != 0) {
            employee.setEmail(generateEmail(employee.getName()+emailCounter, employee.getSurname()));
        }
    }


    /**
     * Create a new employee with the given name and surname.
     *
     * @param name    the name of the employee (must not be null)
     * @param surname the surname of the employee (must not be null)
     * @return the created supervisor entity
     * @throws IllegalArgumentException if the name or surname is null
     */
    public Employee createEmployee(@NonNull String name, @NonNull String surname) {
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Employee(name, surname);
    }

    /**
     * Create a new employee with the given name, surname, monthly salary, and employee role.
     *
     * @param name          the name of the supervisor (must not be null)
     * @param surname       the surname of the supervisor (must not be null)
     * @param monthlySalary the monthly salary of the supervisor
     * @param workerRole  the role of the supervisor (must not be null)
     * @return the created supervisor entity
     * @throws IllegalArgumentException if the name, surname, or employee role is null or if the email is not unique
     */
    public Employee createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull WorkerRole workerRole) {
        checkSalary(monthlySalary, workerRole);
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Employee(name, surname, monthlySalary, workerRole);
    }

    private void checkSalary(double monthlySalary, WorkerRole employeeRole) {
       if(monthlySalary < 0) {
           throw new IllegalArgumentException(SALARY_MUST_BE_POSITIVE);
       }
       if(Double.compare(monthlySalary, employeeRole.getMonthlySalary()) < 0) {
           throw new IllegalArgumentException(SALARY_MUST_BE_AT_LEAST_ROLE_MINIMUM + employeeRole);
       }
    }
    /**
     * Find an employee by their ID.
     *
     * @param employeeId the ID of the employee
     * @throws IllegalArgumentException if the employeeId is null and the employee does not exist
     * @return Optional containing the employee if found
     */
    public Optional<Employee> findEmployeeById(@NonNull Long employeeId) {
        return Optional.of(getEmployeeOrThrow(employeeId));

    }

    /**
     * Retrieve all employees.
     *
     * @return list of all employees
     */
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Delete an employee by ID.
     *
     * @param employeeId the ID of the employee to delete
     * @throws IllegalArgumentException if the employeeId is null
     */
    public void deleteEmployeeById(@NonNull Long employeeId) {
        assertNotNull(employeeId, NULL_EMPLOYEE_ID);
        employeeRepository.deleteById(employeeId);
    }

    /**
     * Retrieve all tasks assigned to an employee in a specific state.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @throws IllegalArgumentException if the employeeId or taskState is null
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeAndTaskState(@NonNull Long employeeId,@NonNull TaskState taskState) {
        assertNotNull(employeeId, NULL_EMPLOYEE_ID);
        assertNotNull(taskState, NULL_TASK_STATE);
        return employeeRepository.findTasksByEmployeeByTaskState(employeeId, taskState);
    }

    /**  Retrieve all tasks assigned to an employee in a specific state, ordered by start date.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @param startDate the start date to filter
     * @throws IllegalArgumentException if the employeeId or taskState is null and if the startDate is null
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeByTaskStateByStartDate(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate startDate) {
        getEmployeeOrThrow(employeeId);
        assertNotNull(taskState, NULL_TASK_STATE);
        assertNotNull(startDate, NULL_DATE);
        return employeeRepository.findTasksByEmployeeByTaskStateByStartDate(employeeId, taskState, startDate);
    }

    /**  Retrieve all tasks assigned to an employee in a specific state, ordered by end date.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @param endDate the end date to filter
     * @throws IllegalArgumentException if the employeeId or taskState is null and if the endDate is null
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeByTaskStateByEndDate(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate endDate) {
        getEmployeeOrThrow(employeeId);
        assertNotNull(endDate, NULL_DATE);
        assertNotNull(taskState, NULL_TASK_STATE);
        return employeeRepository.findTasksByEmployeeByTaskStateByEndDate(employeeId, taskState, endDate);
    }

    /**  Retrieve all tasks assigned to an employee in a specific state, between start date and end date.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @param startDate the start date to filter
     * @param endDate the end date to filter
     * @throws IllegalArgumentException if the employeeId or taskState is null and if the startDate or endDate is null
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeByTaskStateBetweenStartDateAndEndDate(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        getEmployeeOrThrow(employeeId);
        assertNotNull(startDate, NULL_DATE);
        assertNotNull(endDate, NULL_DATE);
        assertNotNull(taskState, NULL_TASK_STATE);
        return employeeRepository.findTasksByEmployeeByTaskStateBetweenDates(employeeId, taskState, startDate, endDate);
    }

    /**  Retrieve all tasks assigned to an employee in a specific state, ordered by start date descending.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @throws IllegalArgumentException if the employeeId or taskState is null and if the startDate is null
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeByTaskStateOrderByStartDateDesc(@NonNull Long employeeId, @NonNull TaskState taskState) {
        getEmployeeOrThrow(employeeId);
        assertNotNull(taskState, NULL_TASK_STATE);
        return employeeRepository.findTasksByEmployeeByTaskStateOrderByStartDateDesc(employeeId, taskState);
    }

    /**  Retrieve all tasks assigned to an employee in a specific state, ordered by end date descending.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @throws IllegalArgumentException if the employeeId or taskState is null and if the endDate is null
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeByTaskStateOrderByEndDateDesc(@NonNull Long employeeId, @NonNull TaskState taskState) {
        getEmployeeOrThrow(employeeId);
        assertNotNull(taskState, NULL_TASK_STATE);
        return employeeRepository.findTasksByEmployeeByTaskStateOrderByEndDateDesc(employeeId, taskState);
    }



    /**
     * Helper method to throw exception if the given object is null.
     *
     * @param obj given object to be checked
     * @param message to be thrown if the given object is null
     * @throws IllegalArgumentException if the supervisor does not exist
     */
    protected void assertNotNull(Object obj, String message) throws IllegalArgumentException{
        if(obj == null) throw new IllegalArgumentException(message);
    }



    /**
     * Check that an employee exists.
     *
     * @param employeeId the employee ID
     * @return the employee if found
     * @throws IllegalArgumentException if the employee does not exist
     */
    private Employee     getEmployeeOrThrow(Long employeeId) {
        assertNotNull(employeeId, NULL_EMPLOYEE_ID);
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYEE_NOT_FOUND));
    }
}
