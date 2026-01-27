package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.*;
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
     * Retrieves all tasks of a specific employee that are in a given state.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @return a list of tasks assigned to the employee in the specified state.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.workerId = :employeeId AND t.taskState = :taskState")
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
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.workerId = :employeeId AND t.taskState = :taskState AND t.startDate = :startDate")
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
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.workerId = :employeeId AND t.taskState = :taskState AND t.endDate = :endDate")
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
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.workerId = :employeeId AND t.taskState = :taskState AND t.startDate <= :endDate AND t.endDate >= :startDate")
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
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.workerId = :employeeId AND t.taskState = :taskState ORDER BY t.startDate DESC")
    List<Task> findTasksByEmployeeByTaskStateOrderByStartDateDesc(@Param("employeeId") Long employeeId,
                                                                  @Param("taskState") TaskState taskState);

    /**
     * Retrieves all tasks of a specific employee that are in a given state, ordered by start date descending.
     *
     * @param employeeId the ID of the employee.
     * @param taskState  the state of the tasks to filter by.
     * @return a list of tasks assigned to the employee ordered by endDate.
     */
    @Query("SELECT t FROM employee d JOIN d.tasks t WHERE d.workerId = :employeeId AND t.taskState = :taskState ORDER BY t.endDate DESC")
    List<Task> findTasksByEmployeeByTaskStateOrderByEndDateDesc(@Param("employeeId") Long employeeId,
                                                              @Param("taskState") TaskState taskState);
    /**
     * Counts the number of employees whose email starts with a given prefix.
     * The comparison is case-insensitive.
     *
     * @param emailPrefix the email prefix to search for.
     * @return the count of employees with emails starting with the prefix.
     */
    @Query("SELECT COUNT(d) FROM worker d WHERE LOWER(d.email) LIKE LOWER(CONCAT(:emailPrefix, '%'))")
    int countEmailsStartingWithEmailPrefix(@Param("emailPrefix") String emailPrefix);
}
