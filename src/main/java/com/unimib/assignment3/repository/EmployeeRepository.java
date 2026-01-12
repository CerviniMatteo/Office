package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findEmployeeByMonthlySalary(Double monthlySalary);
    List<Employee> findEmployeeByMonthlySalaryOrderByEmployeeRoleAsc(Double monthlySalary);
    List<Employee> findEmployeeByMonthlySalaryOrderByEmployeeRoleDesc(Double monthlySalary);
    List<Employee> findEmployeeByEmployeeRole(EmployeeRole employeeRole);
    List<Employee> findEmployeeByEmployeeRoleOrderByMonthlySalaryAsc(EmployeeRole employeeRole);
    List<Employee> findEmployeeByEmployeeRoleOrderByMonthlySalaryDesc(EmployeeRole employeeRole);
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.personId = :employeeId AND t.taskState = :taskState")
    List<Task> findTasksByEmployeeAndTaskState(@Param("employeeId") Long employeeId,
                                               @Param("taskState") TaskState taskState);

    @Query("SELECT COUNT(d) FROM employee d WHERE LOWER(d.email) LIKE LOWER(CONCAT(:emailPrefix, '%'))")
    int countEmailsStartingWithEmailPrefix(@Param("emailPrefix") String emailPrefix);
}