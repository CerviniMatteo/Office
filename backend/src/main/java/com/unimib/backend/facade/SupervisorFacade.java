package com.unimib.backend.facade;

import com.unimib.backend.POJO.Employee;
import com.unimib.backend.POJO.Supervisor;
import com.unimib.backend.POJO.Worker;
import com.unimib.backend.enums.WorkerRole;
import com.unimib.backend.service.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupervisorFacade {

    @Autowired
    private SupervisorService supervisorService;

    public void fireEmployee(@NonNull Long managerId, @NonNull Long employeeId) {
        supervisorService.fireWorker(managerId, employeeId);
    }

    public void fireEmployees(@NonNull Long managerId, @NonNull List<Employee> employees) {
        supervisorService.fireWorkers(managerId, employees);
    }

    public List<? extends Worker> findWorkersByMonthlySalary(@NonNull Long employeeId, double monthlySalary) {
        return supervisorService.findWorkersByMonthlySalary(employeeId, monthlySalary);
    }

    public List<? extends Worker> findWorkersByMonthlySalaryAscByWorkerRole(@NonNull Long employeeId, double monthlySalary) {
        return supervisorService.findWorkersByMonthlySalaryOrderByWorkerRoleAsc(employeeId, monthlySalary);
    }

    public List<? extends Worker> findWorkersByMonthlySalaryDescByWorkerRole(@NonNull Long employeeId, double monthlySalary) {
        return supervisorService.findWorkersByMonthlySalaryOrderByWorkerRoleDesc(employeeId, monthlySalary);
    }

    public List<? extends Worker> findWorkersByWorkerRole(@NonNull Long workerId, @NonNull WorkerRole workerRole) {
        return supervisorService.findWorkersByWorkerRole(workerId, workerRole);
    }

    public List<? extends Worker> findWorkersByWorkerRoleAscByMonthlySalary(@NonNull Long workerId, @NonNull WorkerRole workerRole) {
        return supervisorService.findWorkersByWorkerRoleOrderByMonthlySalaryAsc(workerId, workerRole);
    }

    public List<? extends Worker> findWorkersByWorkerRoleDescByMonthlySalary(@NonNull Long workerId, @NonNull WorkerRole workerRole) {
        return supervisorService.findWorkersByWorkerRoleOrderByMonthlySalaryDesc(workerId, workerRole);
    }

    public void updateMonthlySalaryById(@NonNull Long managerId, @NonNull Long workerId, double monthlySalary) {
        supervisorService.updateMonthlySalaryById(managerId, workerId, monthlySalary);
    }

    public void updateWorkerRoleById(@NonNull Long managerId, @NonNull Long workerId, @NonNull WorkerRole workerRole) {
        supervisorService.updateWorkerRoleById(managerId, workerId, workerRole);
    }

    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname) {
        return supervisorService.createSupervisor(name, surname);
    }

    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, @NonNull WorkerRole workerRole) {
        return supervisorService.createSupervisor(name, surname, workerRole);
    }

    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull WorkerRole workerRole) {
        return supervisorService.createSupervisor(name, surname, monthlySalary, workerRole);
    }

    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull WorkerRole workerRole, @NonNull Supervisor supervisor, List<Supervisor> subordinates) {
        return supervisorService.createSupervisor(name, surname, monthlySalary, workerRole, supervisor, subordinates);
    }

    public Supervisor saveSupervisor(@NonNull Supervisor supervisor) {
        return supervisorService.saveSupervisor(supervisor);
    }

    public Optional<Supervisor> findSupervisorById(@NonNull Long supervisorId) {
        return supervisorService.findSupervisorById(supervisorId);
    }

    public List<Supervisor> findAllSupervisors() {
        return supervisorService.findAllSupervisors();
    }

    public void deleteSupervisorById(@NonNull Long supervisorId) {
        supervisorService.deleteWorkerById(supervisorId);
    }

    public void assignSubordinate(@NonNull Long supervisorId, @NonNull Long subordinateId) {
        supervisorService.assignSubordinate(supervisorId, subordinateId);
    }

    public void removeSubordinate(@NonNull Long supervisorId, @NonNull Long subordinateId) {
        supervisorService.removeSubordinate(supervisorId, subordinateId);
    }

    public List<Supervisor> findSupervisorsWithoutSupervisor() {
        return supervisorService.findSupervisorsWithoutSupervisor();
    }

    public List<Supervisor> findSupervisorsWithoutSubordinates() {
        return supervisorService.findSupervisorsWithoutSubordinates();
    }

    public List<Supervisor> findSupervisorsWithoutSupervisedTeam() {
        return supervisorService.findSupervisorsWithoutSupervisedTeam();
    }
}