package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Query per trovare task per stato
    List<Task> findByTaskState(TaskState taskState);

    // Query per contare task per stato
    long countByTaskState(TaskState taskState);

    // Query custom per trovare task assegnati a un dipendente specifico
    @Query("SELECT t FROM task t JOIN t.assignedEmployees d WHERE d = :employee")
    List<Task> findTasksByEmployee(@Param("employee") Employee employee);

    // Query per trovare task senza dipendenti assegnati
    @Query("SELECT t FROM task t WHERE SIZE(t.assignedEmployees) = 0")
    List<Task> findTasksWithoutEmployee();

    // Query per trovare task con più di N dipendenti assegnati
    @Query("SELECT t FROM task t WHERE SIZE(t.assignedEmployees) > :minEmployees")
    List<Task> findTasksWithMoreThanNEmployees(@Param("minEmployees") int minEmployees);

    // Query per trovare task per stato e con dipendenti assegnati
    @Query("SELECT t FROM task t WHERE t.taskState = :taskState AND SIZE(t.assignedEmployees) > 0")
    List<Task> findTasksByStateWithEmployees(@Param("taskState") TaskState taskState);

    // Query per contare i dipendenti assegnati a un task specifico
    @Query("SELECT SIZE(t.assignedEmployees) FROM task t WHERE t.taskId = :taskId")
    int countEmployeesByTaskId(@Param("taskId") Long taskId);

    // Query per trovare tutti i task di un certo stato con un numero specifico di dipendenti
    @Query("SELECT t FROM task t WHERE t.taskState = :taskState AND SIZE(t.assignedEmployees) = :numEmployees")
    List<Task> findTasksByStateAndEmployeesCount(@Param("taskState") TaskState taskState, @Param("numEmployees") int numEmployees);

    // Query per trovare tutti i task assegnati a un team specifico
    @Query("SELECT t FROM team tm JOIN tm.tasks t WHERE tm.teamId = :teamId")
    List<Task> findTasksByTeamId(@Param("teamId") Long teamId);
}