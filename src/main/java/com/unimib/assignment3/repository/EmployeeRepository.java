package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing {@link Employee} entities.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Retrieves all employees with a specific monthly salary.
     *
     * @param monthlySalary the monthly salary to filter employees by.
     * @return a list of employees with the given monthly salary.
     */
    List<Employee> findEmployeeByMonthlySalary(Double monthlySalary);

    /**
     * Retrieves all employees with a specific monthly salary, ordered by role ascending.
     *
     * @param monthlySalary the monthly salary to filter employees by.
     * @return a list of employees ordered by role in ascending order.
     */
    List<Employee> findEmployeeByMonthlySalaryOrderByEmployeeRoleAsc(Double monthlySalary);

    /**
     * Retrieves all employees with a specific monthly salary, ordered by role descending.
     *
     * @param monthlySalary the monthly salary to filter employees by.
     * @return a list of employees ordered by role in descending order.
     */
    List<Employee> findEmployeeByMonthlySalaryOrderByEmployeeRoleDesc(Double monthlySalary);

    /**
     * Retrieves all employees with a specific role.
     *
     * @param employeeRole the role to filter employees by.
     * @return a list of employees with the given role.
     */
    List<Employee> findEmployeeByEmployeeRole(EmployeeRole employeeRole);

    /**
     * Retrieves all employees with a specific role, ordered by monthly salary ascending.
     *
     * @param employeeRole the role to filter employees by.
     * @return a list of employees ordered by monthly salary in ascending order.
     */
    List<Employee> findEmployeeByEmployeeRoleOrderByMonthlySalaryAsc(EmployeeRole employeeRole);

    /**
     * Retrieves all employees with a specific role, ordered by monthly salary descending.
     *
     * @param employeeRole the role to filter employees by.
     * @return a list of employees ordered by monthly salary in descending order.
     */
    List<Employee> findEmployeeByEmployeeRoleOrderByMonthlySalaryDesc(EmployeeRole employeeRole);

    /**
     * Retrieves all tasks of a specific employee that are in a given state.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @return a list of tasks assigned to the employee in the specified state.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.personId = :employeeId AND t.taskState = :taskState")
    List<Task> findTasksByEmployeeByTaskState(@Param("employeeId") Long employeeId,
                                              @Param("taskState") TaskState taskState);

    /**
     * Retrieves all tasks of a specific employee that are in a given state and a starting date.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @param startDate the start date to order the tasks by.
     * @return a list of tasks assigned to the employee in a given startDate.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.personId = :employeeId AND t.taskState = :taskState AND t.startDate = :startDate")
    List<Task> findTasksByEmployeeByTaskStateByStartDate(@Param("employeeId") Long employeeId,
                                                                @Param("taskState") TaskState taskState,
                                                                @Param("startDate") LocalDate startDate);

    /**
     * Retrieves all tasks of a specific employee that are in a given state and an ending date.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @param endDate the start date to order the tasks by.
     * @return a list of tasks assigned to the employee in a given startDate.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.personId = :employeeId AND t.taskState = :taskState AND t.endDate = :endDate")
    List<Task> findTasksByEmployeeByTaskStateByEndDate(@Param("employeeId") Long employeeId,
                                                         @Param("taskState") TaskState taskState,
                                                         @Param("endDate") LocalDate endDate);

    /**
     * Retrieves all tasks of a specific employee that are in a given state, between dates.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @param startDate the start date to order the tasks by.
     * @param endDate the start date to order the tasks by.
     * @return a list of tasks assigned to the employee in a given startDate.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.personId = :employeeId AND t.taskState = :taskState AND t.startDate <= :endDate AND t.endDate >= :startDate")
    List<Task> findTasksByEmployeeByTaskStateBetweenDates(@Param("employeeId") Long employeeId,
                                                          @Param("taskState") TaskState taskState,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);


    /**
     * Retrieves all tasks of a specific employee that are in a given state, ordered by start date descending.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @return a list of tasks assigned to the employee ordered by startState.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.personId = :employeeId AND t.taskState = :taskState ORDER BY t.startDate DESC")
    List<Task> findTasksByEmployeeByTaskStateOrderByStartDateDesc(@Param("employeeId") Long employeeId,
                                                                  @Param("taskState") TaskState taskState);

    /**
     * Retrieves all tasks of a specific employee that are in a given state, ordered by start date descending.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @return a list of tasks assigned to the employee ordered by endDate.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.personId = :employeeId AND t.taskState = :taskState ORDER BY t.endDate DESC")
    List<Task> findTasksByEmployeeByTaskStateOrderByEndDateDesc(@Param("employeeId") Long employeeId,
                                                              @Param("taskState") TaskState taskState);
    /**
     * Counts the number of employees whose email starts with a given prefix.
     * The comparison is case-insensitive.
     *
     * @param emailPrefix the email prefix to search for.
     * @return the count of employees with emails starting with the prefix.
     */
    @Query("SELECT COUNT(d) FROM employee d WHERE LOWER(d.email) LIKE LOWER(CONCAT(:emailPrefix, '%'))")
    int countEmailsStartingWithEmailPrefix(@Param("emailPrefix") String emailPrefix);
}
