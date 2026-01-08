package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.DipendenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.unimib.assignment3.constants.EmployeeConstants.NOT_A_MANAGER;
import static com.unimib.assignment3.constants.EmployeeConstants.NULL_EMPLOYEE;

/**
 * Service class for managing Dipendente (employee) entities.
 * <p>
 * Provides CRUD operations, salary and role updates, task queries, and manager-authorized actions.
 */
@Service
public class DipendenteService {

    @Autowired
    private DipendenteRepository dipendenteRepository;

    /**
     * Save a single employee to the database.
     *
     * @param dipendente the employee to save
     * @return the saved employee
     */
    public Dipendente saveEmployee(Dipendente dipendente) {
        return dipendenteRepository.save(dipendente);
    }

    /**
     * Save multiple employees to the database in bulk.
     *
     * @param employees list of employees
     * @return list of saved employees
     */
    public List<Dipendente> saveAllEmployees(List<Dipendente> employees) {
        return dipendenteRepository.saveAll(employees);
    }

    /**
     * Find an employee by their ID.
     *
     * @param employeeId the ID of the employee
     * @return Optional containing the employee if found
     */
    public Optional<Dipendente> findEmployeeById(Long employeeId) {
        return dipendenteRepository.findById(employeeId);
    }

    /**
     * Retrieve all employees.
     *
     * @return list of all employees
     */
    public List<Dipendente> findAllEmployees() {
        return dipendenteRepository.findAll();
    }

    /**
     * Delete an employee by ID.
     *
     * @param employeeId the ID of the employee to delete
     */
    public void deleteEmployeeById(Long employeeId) {
        dipendenteRepository.deleteById(employeeId);
    }

    /**
     * Fire (delete) a single employee.
     * Only a manager can perform this action.
     *
     * @param managerId  the manager's ID
     * @param employeeId the employee's ID to fire
     */
    public void fireEmployee(Long managerId, Long employeeId) {
        checkManager(managerId);  // ensure caller is a manager
        deleteEmployeeById(employeeId);
    }

    /**
     * Fire multiple employees at once.
     * Only a manager can perform this action.
     *
     * @param managerId the manager's ID
     * @param employees list of employees to fire
     */
    public void fireEmployees(Long managerId, List<Dipendente> employees) {
        checkManager(managerId);
        employees.forEach(employee -> deleteEmployeeById(employee.getId()));
    }

    /**
     * Find employees with a specific monthly salary.
     * Only accessible by managers.
     *
     * @param employeeId    the manager's ID
     * @param monthlySalary the target salary
     * @return list of employees with that salary
     */
    public List<Dipendente> findEmployeesByMonthlySalary(Long employeeId, Double monthlySalary) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByMonthlySalary(monthlySalary);
    }

    /**
     * Find employees by salary and sort by role ascending.
     */
    public List<Dipendente> findEmployeesByMonthlySalaryOrderByEmployeeRoleAsc(Long employeeId, Double monthlySalary) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByMonthlySalaryOrderByEmployeeRoleAsc(monthlySalary);
    }

    /**
     * Find employees by salary and sort by role descending.
     */
    public List<Dipendente> findEmployeesByMonthlySalaryOrderByEmployeeRoleDesc(Long employeeId, Double monthlySalary) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByMonthlySalaryOrderByEmployeeRoleDesc(monthlySalary);
    }

    /**
     * Find employees by role.
     * Only accessible by managers.
     */
    public List<Dipendente> findEmployeesByEmployeeRole(Long employeeId, EmployeeRole employeeRole) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByEmployeeRole(employeeRole);
    }

    /**
     * Find employees by role, ordered by salary ascending.
     */
    public List<Dipendente> findEmployeesByEmployeeRoleOrderByMonthlySalaryAsc(Long employeeId, EmployeeRole employeeRole) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByEmployeeRoleOrderByMonthlySalaryAsc(employeeRole);
    }

    /**
     * Find employees by role, ordered by salary descending.
     */
    public List<Dipendente> findEmployeesByEmployeeRoleOrderByMonthlySalaryDesc(Long employeeId, EmployeeRole employeeRole) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByEmployeeRoleOrderByMonthlySalaryDesc(employeeRole);
    }

    /**
     * Retrieve all tasks assigned to an employee in a specific state.
     *
     * @param employeeId the employee's ID
     * @param taskState  the state of tasks to filter
     * @return list of tasks
     */
    public List<Task> findTasksByEmployeeAndTaskState(Long employeeId, TaskState taskState) {
        return dipendenteRepository.findTasksByEmployeeAndTaskState(employeeId, taskState);
    }

    /**
     * Update an employee's monthly salary.
     * Only a manager can perform this operation.
     *
     * @param managerId    the manager's ID
     * @param employeeId   the employee's ID
     * @param monthlySalary new monthly salary
     */
    public void updateMonthlySalaryById(Long managerId, Long employeeId, Double monthlySalary) {
        checkManager(managerId);
        Dipendente employee = checkEmployeeIsNull(employeeId);
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
    public void updateEmployeeRoleById(Long managerId, Long employeeId, EmployeeRole employeeRole) {
        checkManager(managerId);
        Dipendente employee = checkEmployeeIsNull(employeeId);
        employee.setEmployeeRole(employeeRole);
    }

    /**
     * Check that the given employee is a manager.
     *
     * @param managerId the employee ID to check
     * @throws IllegalArgumentException if the employee is not a manager
     */
    private void checkManager(Long managerId) {
        Dipendente manager = checkEmployeeIsNull(managerId);
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
    private Dipendente checkEmployeeIsNull(Long employeeId) {
        return dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));
    }
}
