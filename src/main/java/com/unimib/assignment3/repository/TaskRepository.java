package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Dipendente;
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
    @Query("SELECT t FROM task t JOIN t.dipendentiAssegnati d WHERE d = :dipendente")
    List<Task> findTasksByDipendente(@Param("dipendente") Dipendente dipendente);

    // Query per trovare task senza dipendenti assegnati
    @Query("SELECT t FROM task t WHERE SIZE(t.dipendentiAssegnati) = 0")
    List<Task> findTasksWithoutDipendenti();

    // Query per trovare task con più di N dipendenti assegnati
    @Query("SELECT t FROM task t WHERE SIZE(t.dipendentiAssegnati) > :minDipendenti")
    List<Task> findTasksWithMoreThanNDipendenti(@Param("minDipendenti") int minDipendenti);

    // Query per trovare task per stato e con dipendenti assegnati
    @Query("SELECT t FROM task t WHERE t.taskState = :stato AND SIZE(t.dipendentiAssegnati) > 0")
    List<Task> findTasksByStateWithDipendenti(@Param("stato") TaskState stato);

    // Query per contare i dipendenti assegnati a un task specifico
    @Query("SELECT SIZE(t.dipendentiAssegnati) FROM task t WHERE t.taskId = :taskId")
    int countDipendentiByTaskId(@Param("taskId") Long taskId);

    // Query per trovare tutti i task di un certo stato con un numero specifico di dipendenti
    @Query("SELECT t FROM task t WHERE t.taskState = :stato AND SIZE(t.dipendentiAssegnati) = :numDipendenti")
    List<Task> findTasksByStateAndDipendentiCount(@Param("stato") TaskState stato, @Param("numDipendenti") int numDipendenti);

    // Query per trovare tutti i task assegnati a un team specifico
    @Query("SELECT t FROM team tm JOIN tm.tasks t WHERE tm.idTeam = :idTeam")
    List<Task> findTasksByTeamId(@Param("idTeam") Long idTeam);
}