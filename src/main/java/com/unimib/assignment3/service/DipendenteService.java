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

@Service
public class DipendenteService {

    @Autowired
    DipendenteRepository dipendenteRepository;

    public Dipendente saveEmployee(Dipendente dipendente) {
        return dipendenteRepository.save(dipendente);
    }

    public List<Dipendente> saveAllEmployees(List<Dipendente> dipendenti) {
        return dipendenteRepository.saveAll(dipendenti);
    }

    public Optional<Dipendente> findById(Long id) {
        return dipendenteRepository.findById(id);
    }

    public Dipendente getReferenceById(Long id) {
        return dipendenteRepository.getReferenceById(id);
    }

    public List<Dipendente> findAll() {
        return dipendenteRepository.findAll();
    }

    public boolean existById(Long id) {
        return dipendenteRepository.existsById(id);
    }

    public long countAll() {
        return dipendenteRepository.count();
    }

    public void deleteById(Long id) {
        dipendenteRepository.deleteById(id);
    }

    public void deleteEmployee(Dipendente dipendente) {
        dipendenteRepository.delete(dipendente);
    }

    public void deleteAllByList(List<Dipendente> dipendenti) {
        dipendenteRepository.deleteAll(dipendenti);
    }

    public void deleteAll() {
        dipendenteRepository.deleteAll();
    }

    public void flush() {
        dipendenteRepository.flush();
    }

    public long countSupervisor() {
        return dipendenteRepository.count(); // Se vuoi contare solo i manager, serve query custom
    }

    public List<Dipendente> findEmployeesByMonthlySalary(Long employeeId, Double monthlySalary) {
        Dipendente manager = dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));

        if (checkRole(manager.getEmployeeRole(), EmployeeRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }

        return dipendenteRepository.findByMonthlySalaryOrderByMonthlySalaryAsc(monthlySalary);
    }

    public List<Dipendente> findEmployeesWithSalaryGreaterThan(Long employeeId, Double monthlySalary) {
        Dipendente manager = dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));

        if (checkRole(manager.getEmployeeRole(), EmployeeRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }

        return dipendenteRepository.findByMonthlySalaryGreaterThanOrderByMonthlySalaryAsc(monthlySalary);
    }

    public List<Dipendente> findEmployeesWithSalaryLessThan(Long employeeId, Double monthlySalary) {
        Dipendente manager = dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));

        if (checkRole(manager.getEmployeeRole(), EmployeeRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }

        return dipendenteRepository.findByMonthlySalaryLessThanOrderByMonthlySalaryAsc(monthlySalary);
    }

    public List<Dipendente> findEmployeesWithSalaryBetween(Long employeeId, Double min, Double max) {
        Dipendente manager = dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));

        if (checkRole(manager.getEmployeeRole(), EmployeeRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }

        return dipendenteRepository.findByMonthlySalaryBetweenOrderByMonthlySalaryAsc(min, max);
    }

    public List<Dipendente> findEmployeesByEmployeeRole(Long employeeId, EmployeeRole employeeRole) {
        Dipendente manager = dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));

        if (checkRole(manager.getEmployeeRole(), EmployeeRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }

        return dipendenteRepository.findByEmployeeRoleOrderByMonthlySalaryAsc(employeeRole);
    }

    public List<Task> findTasksByDipendenteId(Long dipendenteId) {
        return dipendenteRepository.findTasksById(dipendenteId);
    }

    public List<Task> findTasksByDipendenteAndState(Long dipendenteId, TaskState taskState) {
        return dipendenteRepository.findTasksByDipendenteAndState(dipendenteId, taskState);
    }

    public void fireEmployee(Long managerId, Long employeeId) {
        Dipendente manager = dipendenteRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));
        Dipendente employee = dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));
        if (checkRole(manager.getEmployeeRole(), EmployeeRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }

        deleteById(employeeId);
    }

    public int updateMonthlySalary(Long dipendenteId, Double monthlySalary) {
        return dipendenteRepository.updateMonthlySalaryById(dipendenteId, monthlySalary);
    }

    public int updateEmployeeRoleAndMonthlySalary(Long dipendenteId, Double monthlySalary, EmployeeRole employeeRole) {
        return dipendenteRepository.updateEmployeeRoleAndMonthlySalaryById(dipendenteId, monthlySalary, employeeRole);
    }

    private boolean checkRole(EmployeeRole managerRole, EmployeeRole employeeRole) {
        return managerRole != employeeRole;
    }
}

