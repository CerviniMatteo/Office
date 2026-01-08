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

    public List<Dipendente> saveAllEmployees(List<Dipendente> employees) {
        return dipendenteRepository.saveAll(employees);
    }

    public Optional<Dipendente> findEmployeeById(Long employeeId) {
        return dipendenteRepository.findById(employeeId);
    }

    public List<Dipendente> findAllEmployees() {
        return dipendenteRepository.findAll();
    }

    public void deleteEmployeeById(Long employeeId) {
        dipendenteRepository.deleteById(employeeId);
    }

    public void fireEmployee(Long managerId, Long employeeId) {
        checkManager(managerId);

        deleteEmployeeById(employeeId);
    }

    public void fireEmployees(Long managerId, List<Dipendente> employees) {
        checkManager(managerId);
        employees.forEach(employee -> deleteEmployeeById(employee.getId()));
    }

    public List<Dipendente> findEmployeesByMonthlySalary(Long employeeId, Double monthlySalary) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByMonthlySalaryOrderByEmployeeRoleAsc(monthlySalary);
    }

    public List<Dipendente> findEmployeesByEmployeeRole(Long employeeId, EmployeeRole employeeRole) {
        checkManager(employeeId);
        return dipendenteRepository.findDipendenteByEmployeeRoleOrderByMonthlySalaryAsc(employeeRole);
    }

    public List<Task> findTasksByEmployeeAndTaskState(Long employeeId, TaskState taskState) {
        return dipendenteRepository.findTasksByEmployeeAndTaskState(employeeId, taskState);
    }

    public void updateMonthlySalaryById(Long managerId, Long employeeId, Double monthlySalary){
        checkManager(managerId);
        Dipendente employee = checkEmployeeIsNull(employeeId);
        employee.setMonthlySalary(monthlySalary);
    }

    public void updateEmployeeRoleById(Long managerId, Long employeeId, EmployeeRole employeeRole){
        checkManager(managerId);
        Dipendente employee = checkEmployeeIsNull(employeeId);
        employee.setEmployeeRole(employeeRole);
    }

    private void checkManager(Long managerId){
        Dipendente manager = checkEmployeeIsNull(managerId);
        if(!manager.getEmployeeRole().equals(EmployeeRole.MANAGER)){
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }
    }

    private Dipendente checkEmployeeIsNull(Long employeeId){
        return dipendenteRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));
    }
}
