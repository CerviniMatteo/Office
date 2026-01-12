package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Persona;
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
public class DipendenteService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Save a single employee to the database and handles email uniqueness.
     *
     * @param employee the employee to save
     * @return the saved employee
     */
    public Dipendente saveEmployee(@NonNull Dipendente employee) {
        Objects.requireNonNull(employee, NULL_EMPLOYEE);

        int emailCounter = employeeRepository.countEmailsStartingWithEmailPrefix(employee.getNome());
        if(emailCounter != 0) {
            employee.setEmail(Persona.generateEmail(employee.getNome(), employee.getCognome(), emailCounter));
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
    public Dipendente createEmployee(@NonNull String name, @NonNull String surname) {
        Objects.requireNonNull(name, NULL_NAME);
        Objects.requireNonNull(surname, NULL_SURNAME);
        return new Dipendente(name, surname);
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
    public Dipendente createEmployee(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        Objects.requireNonNull(name, NULL_NAME);
        Objects.requireNonNull(surname, NULL_SURNAME);
        return new Dipendente(name, surname, employeeRole);
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
    public Dipendente createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        Objects.requireNonNull(name, NULL_NAME);
        Objects.requireNonNull(surname, NULL_SURNAME);
        return new Dipendente(name, surname, monthlySalary, employeeRole);
    }

    /**
     * Save multiple employees to the database in bulk.
     *
     * @param employees list of employees
     * @return list of saved employees
     */
    public List<Dipendente> saveAllEmployees(@NonNull List<Dipendente> employees) {
        Objects.requireNonNull(employees, NULL_EMPLOYEES);
        return employeeRepository.saveAll(employees);
    }

    /**
     * Find an employee by their ID.
     *
     * @param employeeId the ID of the employee
     * @return Optional containing the employee if found
     */
    public Optional<Dipendente> findEmployeeById(@NonNull Long employeeId) {
        return Optional.of(getEmployeeOrThrow(employeeId));

    }

    /**
     * Retrieve all employees.
     *
     * @return list of all employees
     */
    public List<Dipendente> findAllEmployees() {
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
    public void fireEmployees(@NonNull Long managerId,@NonNull List<Dipendente> employees) {
        checkManagerRole(managerId);
        employees.forEach(employee -> deleteEmployeeById(employee.getId()));
    }

    /**
     * Find employees with a specific monthly salary.
     * Only accessible by managers.
     *
     * @param managerId    the manager's ID
     * @param monthlySalary the target salary
     * @return list of employees with that salary
     */
    public List<Dipendente> findEmployeesByMonthlySalary(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return employeeRepository.findDipendenteByMonthlySalary(monthlySalary);
    }

    /**
     * Find employees by salary and sort by role ascending.
     */
    public List<Dipendente> findEmployeesByMonthlySalaryOrderByEmployeeRoleAsc(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return employeeRepository.findDipendenteByMonthlySalaryOrderByEmployeeRoleAsc(monthlySalary);
    }

    /**
     * Find employees by salary and sort by role descending.
     */
    public List<Dipendente> findEmployeesByMonthlySalaryOrderByEmployeeRoleDesc(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return employeeRepository.findDipendenteByMonthlySalaryOrderByEmployeeRoleDesc(monthlySalary);
    }

    /**
     * Find employees by role.
     * Only accessible by managers.
     */
    public List<Dipendente> findEmployeesByEmployeeRole(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        checkManagerRole(employeeId);
        Objects.requireNonNull(employeeRole, CommonConstants.NULL_EMPLOYEE_ROLE);
        return employeeRepository.findDipendenteByEmployeeRole(employeeRole);
    }

    /**
     * Find employees by role, ordered by salary ascending.
     */
    public List<Dipendente> findEmployeesByEmployeeRoleOrderByMonthlySalaryAsc(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        checkManagerRole(employeeId);
        Objects.requireNonNull(employeeRole, CommonConstants.NULL_EMPLOYEE_ROLE);
        return employeeRepository.findDipendenteByEmployeeRoleOrderByMonthlySalaryAsc(employeeRole);
    }

    /**
     * Find employees by role, ordered by salary descending.
     */
    public List<Dipendente> findEmployeesByEmployeeRoleOrderByMonthlySalaryDesc(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        checkManagerRole(employeeId);
        Objects.requireNonNull(employeeRole, CommonConstants.NULL_EMPLOYEE_ROLE);
        return employeeRepository.findDipendenteByEmployeeRoleOrderByMonthlySalaryDesc(employeeRole);
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
        Dipendente employee = getEmployeeOrThrow(employeeId);
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
        Dipendente employee = getEmployeeOrThrow(employeeId);
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
        Dipendente manager = getEmployeeOrThrow(managerId);
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
    private Dipendente getEmployeeOrThrow(Long employeeId) {
        Objects.requireNonNull(employeeId, NULL_EMPLOYEE_ID);
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));
    }
}
