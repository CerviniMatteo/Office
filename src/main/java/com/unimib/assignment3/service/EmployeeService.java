package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Person;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.constants.CommonConstants;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.unimib.assignment3.constants.CommonConstants.*;
import static com.unimib.assignment3.constants.EmployeeConstants.*;
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
     * @return the saved employee
     */
    public Employee saveEmployee(@NonNull Employee employee) {
        Objects.requireNonNull(employee, NULL_EMPLOYEE);

        int emailCounter = employeeRepository.countEmailsStartingWithEmailPrefix(employee.getName());
        if(emailCounter != 0) {
            employee.setEmail(Person.generateEmail(employee.getName(), employee.getSurname(), emailCounter));
        }
        try {
            return employeeRepository.saveAndFlush(employee);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(EMAIL_HAVE_TO_BE_UNIQUE);
        }
    }


    /**
     * Create a new employee with the given name and surname.
     *
     * @param name    the name of the employee (must not be null)
     * @param surname the surname of the employee (must not be null)
     * @return the created supervisor entity
     * @throws NullPointerException if the name or surname is null
     */
    public Employee createEmployee(@NonNull String name, @NonNull String surname) {
        Objects.requireNonNull(name, NULL_NAME);
        Objects.requireNonNull(surname, NULL_SURNAME);
        return new Employee(name, surname);
    }

    /**
     * Create a new employee with the given name, surname, monthly salary, and employee role.
     *
     * @param name          the name of the supervisor (must not be null)
     * @param surname       the surname of the supervisor (must not be null)
     * @param employeeRole  the role of the supervisor (must not be null)
     * @return the created supervisor entity
     * @throws NullPointerException if the name, surname, or employee role is null
     */
    public Employee createEmployee(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        Objects.requireNonNull(name, NULL_NAME);
        Objects.requireNonNull(surname, NULL_SURNAME);
        return new Employee(name, surname, employeeRole);
    }


    /**
     * Create a new employee with the given name, surname, monthly salary, and employee role.
     *
     * @param name          the name of the supervisor (must not be null)
     * @param surname       the surname of the supervisor (must not be null)
     * @param monthlySalary the monthly salary of the supervisor
     * @param employeeRole  the role of the supervisor (must not be null)
     * @return the created supervisor entity
     * @throws NullPointerException if the name, surname, or employee role is null
     */
    public Employee createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        Objects.requireNonNull(name, NULL_NAME);
        Objects.requireNonNull(surname, NULL_SURNAME);
        return new Employee(name, surname, monthlySalary, employeeRole);
    }

    /**
     * Save multiple employees to the database in bulk.
     *
     * @param employees list of employees
     * @return list of saved employees
     */
    public List<Employee> saveAllEmployees(@NonNull List<Employee> employees) {
        Objects.requireNonNull(employees, NULL_EMPLOYEES);
        return employeeRepository.saveAll(employees);
    }

    /**
     * Find an employee by their ID.
     *
     * @param employeeId the ID of the employee
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
     */
    public void deleteEmployeeById(@NonNull Long employeeId) {
        Objects.requireNonNull(employeeId, NULL_EMPLOYEE_ID);
        employeeRepository.deleteById(employeeId);
    }

    /**
     * Fire (delete) a single employee.
     * Only a manager can perform this action.
     *
     * @param managerId  the manager's ID
     * @param employeeId the employee's ID to fire
     */
    public void fireEmployee(@NonNull Long managerId,@NonNull Long employeeId) {
        checkManagerRole(managerId);
        deleteEmployeeById(employeeId);
    }

    /**
     * Fire multiple employees at once.
     * Only a manager can perform this action.
     *
     * @param managerId the manager's ID
     * @param employees list of employees to fire
     */
    public void fireEmployees(@NonNull Long managerId,@NonNull List<Employee> employees) {
        checkManagerRole(managerId);
        employees.forEach(employee -> deleteEmployeeById(employee.getPersonId()));
    }

    /**
     * Find employees with a specific monthly salary.
     * Only accessible by managers.
     *
     * @param managerId    the manager's ID
     * @param monthlySalary the target salary
     * @return list of employees with that salary
     */
    public List<Employee> findEmployeesByMonthlySalary(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return employeeRepository.findEmployeeByMonthlySalary(monthlySalary);
    }

    /**
     * Find employees by salary and sort by role ascending.
     */
    public List<Employee> findEmployeesByMonthlySalaryOrderByEmployeeRoleAsc(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return employeeRepository.findEmployeeByMonthlySalaryOrderByEmployeeRoleAsc(monthlySalary);
    }

    /**
     * Find employees by salary and sort by role descending.
     */
    public List<Employee> findEmployeesByMonthlySalaryOrderByEmployeeRoleDesc(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return employeeRepository.findEmployeeByMonthlySalaryOrderByEmployeeRoleDesc(monthlySalary);
    }

    /**
     * Find employees by role.
     * Only accessible by managers.
     */
    public List<Employee> findEmployeesByEmployeeRole(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        checkManagerRole(employeeId);
        Objects.requireNonNull(employeeRole, CommonConstants.NULL_EMPLOYEE_ROLE);
        return employeeRepository.findEmployeeByEmployeeRole(employeeRole);
    }

    /**
     * Find employees by role, ordered by salary ascending.
     */
    public List<Employee> findEmployeesByEmployeeRoleOrderByMonthlySalaryAsc(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        checkManagerRole(employeeId);
        Objects.requireNonNull(employeeRole, CommonConstants.NULL_EMPLOYEE_ROLE);
        return employeeRepository.findEmployeeByEmployeeRoleOrderByMonthlySalaryAsc(employeeRole);
    }

    /**
     * Find employees by role, ordered by salary descending.
     */
    public List<Employee> findEmployeesByEmployeeRoleOrderByMonthlySalaryDesc(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        checkManagerRole(employeeId);
        Objects.requireNonNull(employeeRole, CommonConstants.NULL_EMPLOYEE_ROLE);
        return employeeRepository.findEmployeeByEmployeeRoleOrderByMonthlySalaryDesc(employeeRole);
    }

    /**
     * Retrieve all tasks assigned to an employee in a specific state.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeAndTaskState(@NonNull Long employeeId,@NonNull TaskState taskState) {
        Objects.requireNonNull(employeeId, NULL_EMPLOYEE_ID);
        Objects.requireNonNull(taskState, NULL_TASK_STATE);
        return employeeRepository.findTasksByEmployeeAndTaskState(employeeId, taskState);
    }

    /**
     * Update an employee's monthly salary.
     * Only a manager can perform this operation.
     *
     * @param managerId    the manager's ID
     * @param employeeId   the employee's ID
     * @param monthlySalary new monthly salary
     */
    public void updateMonthlySalaryById(@NonNull Long managerId,@NonNull Long employeeId, double monthlySalary) {
        checkManagerRole(managerId);
        Employee employee = getEmployeeOrThrow(employeeId);
        employee.setMonthlySalary(monthlySalary);
    }

    /**
     * Update an employee's role.
     * Only a manager can perform this operation.
     *
     * @param managerId    the manager's ID
     * @param employeeId   the employee's ID
     * @param employeeRole the new role
     */
    public void updateEmployeeRoleById(@NonNull Long managerId,@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        checkManagerRole(managerId);
        Objects.requireNonNull(employeeRole, CommonConstants.NULL_EMPLOYEE_ROLE);
        Employee employee = getEmployeeOrThrow(employeeId);
        employee.setEmployeeRole(employeeRole);
    }

    /**
     * Check that the given employee is a manager.
     *
     * @param managerId the employee ID to check
     * @throws IllegalArgumentException if the employee is not a manager
     */
    private void checkManagerRole(Long managerId) {
        Objects.requireNonNull(managerId, NULL_EMPLOYEE_ID);
        Employee manager = getEmployeeOrThrow(managerId);
        if (!manager.getEmployeeRole().equals(EmployeeRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }
    }

    /**
     * Check that an employee exists.
     *
     * @param employeeId the employee ID
     * @return the employee if found
     * @throws IllegalArgumentException if the employee does not exist
     */
    private Employee getEmployeeOrThrow(Long employeeId) {
        Objects.requireNonNull(employeeId, NULL_EMPLOYEE_ID);
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));
    }
}
