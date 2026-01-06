package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DipendenteRepository extends JpaRepository<Dipendente, Long> {

    List<Dipendente> findByMonthlySalaryOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByMonthlySalaryGreaterThanOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByMonthlySalaryLessThanOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByMonthlySalaryBetweenOrderByMonthlySalaryAsc(Double min, Double max);

    List<Dipendente> findByEmployeeRoleOrderByMonthlySalaryAsc(EmployeeRole employeeRole);
    List<Dipendente> findByEmployeeRoleGreaterThanOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByEmployeeRoleLessThanOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByEmployeeRoleBetweenOrderByMonthlySalaryAsc(Double min, Double max);

    @Query("SELECT t FROM dipendente d JOIN d.tasks t WHERE d.id = :dipendenteId AND t.taskState = :taskState")
    List<Task> findTasksByDipendenteAndState(@Param("dipendenteId") Long dipendenteId,
                                             @Param("taskState") TaskState taskState);

    List<Task> findTasksById(Long id);
}
