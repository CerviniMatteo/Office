package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DipendenteRepository extends JpaRepository<Dipendente, Long> {

    List<Dipendente> findByMonthlySalaryOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByMonthlySalaryGreaterThanOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByMonthlySalaryLessThanOrderByMonthlySalaryAsc(Double monthlySalary);
    List<Dipendente> findByMonthlySalaryBetweenOrderByMonthlySalaryAsc(Double min, Double max);

    List<Dipendente> findByEmployeeRoleOrderByMonthlySalaryAsc(EmployeeRole employeeRole);
    List<Dipendente> findByEmployeeRoleGreaterThanOrderByEmployeeRoleAsc(Double monthlySalary);
    List<Dipendente> findByEmployeeRoleLessThanOrderByEmployeeRoleAsc(Double monthlySalary);
    List<Dipendente> findByEmployeeRoleBetweenOrderByEmployeeRoleAsc(Double min, Double max);

    @Query("SELECT t FROM dipendente d JOIN d.tasks t WHERE d.id = :dipendenteId AND t.taskState = :taskState")
    List<Task> findTasksByDipendenteAndState(@Param("dipendenteId") Long dipendenteId,
                                             @Param("taskState") TaskState taskState);

    List<Task> findTasksById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE dipendente d SET d.monthlySalary = :monthlySalary WHERE d.id = :dipendenteId")
    int updateMonthlySalaryById(@Param("dipendenteId") Long dipendenteId,
                                @Param("monthlySalary") Double monthlySalary);

    @Modifying
    @Transactional
    @Query("UPDATE dipendente d SET d.monthlySalary = :monthlySalary, d.employeeRole= :employeeRole WHERE d.id = :dipendenteId")
    int updateEmployeeRoleAndMonthlySalaryById(@Param("dipendenteId") Long dipendenteId,
                                @Param("monthlySalary") Double monthlySalary, @Param("employeeRole") EmployeeRole employeeRole);

}
