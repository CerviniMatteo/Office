package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.TaskState;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(name="task")
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTask;

    @ElementCollection
    @CollectionTable(
            name = "task_dipendenti_assegnati",
            joinColumns = @JoinColumn(name = "task_id")
    )
    @MapKeyJoinColumn(name = "dipendente_id")
    @Column(name = "stato_dipendente")
    private Map<Dipendente, String> dipendentiAssegnati = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "task_state")
    private TaskState taskState;

    public Task() {
        this.taskState = TaskState.DAINIZIARE;
    }

    public Task(Long idTask, TaskState taskState) {
        this.idTask = idTask;
        this.taskState = taskState;
    }

    public Task(Map<Dipendente, String> dipendentiAssegnati, Long idTask, TaskState taskState) {
        this.dipendentiAssegnati = dipendentiAssegnati;
        this.idTask = idTask;
        this.taskState = taskState;
    }



    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public Map<Dipendente, String> getDipendentiAssegnati() {
        return dipendentiAssegnati;
    }

    public void setDipendentiAssegnati(Map<Dipendente, String> dipendentiAssegnati) {
        this.dipendentiAssegnati = dipendentiAssegnati;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }



    public void assegnaDipendente(Dipendente dipendente, String stato) {
        this.dipendentiAssegnati.put(dipendente, stato);
    }

    public void rimuoviDipendente(Dipendente dipendente) {
        this.dipendentiAssegnati.remove(dipendente);
    }

    public String getStatoDipendente(Dipendente dipendente) {
        return this.dipendentiAssegnati.get(dipendente);
    }

    @Override
    public String toString() {
        return "Task{" +
                "idTask=" + idTask +
                ", dipendentiAssegnati=" + dipendentiAssegnati +
                ", taskState=" + taskState +
                '}';
    }
}
