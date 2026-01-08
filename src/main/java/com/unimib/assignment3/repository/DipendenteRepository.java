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

    List<Dipendente> findDipendenteByMonthlySalaryOrderByEmployeeRoleAsc(Double monthlySalary);
    List<Dipendente> findDipendenteByEmployeeRoleOrderByMonthlySalaryAsc(EmployeeRole employeeRole);
    @Query("SELECT t FROM dipendente d JOIN d.tasks t WHERE d.id = :employeeId AND t.taskState = :taskState")
    List<Task> findTasksByEmployeeAndTaskState(@Param("employeeId") Long employeeId,
                                               @Param("taskState") TaskState taskState);

}