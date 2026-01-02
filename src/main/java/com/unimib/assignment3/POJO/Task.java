package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.TaskState;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(name="task")
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTask;

    @ManyToMany
    @JoinTable(
            name = "task_dipendenti",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dipendente_id")
    )
    private List<Dipendente> dipendentiAssegnati = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "task_state")
    private TaskState taskState;

    public Task() {
        this.taskState = TaskState.DAINIZIARE;
        this.dipendentiAssegnati = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();

    }

    public Task(TaskState taskState) {
        this.taskState = taskState != null ? taskState : TaskState.DAINIZIARE;
        this.dipendentiAssegnati = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();
    }

    public Task(Long idTask, TaskState taskState) {
        this.idTask = idTask;
        this.taskState = taskState != null ? taskState : TaskState.DAINIZIARE;
        this.dipendentiAssegnati = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();
    }

    public Task(List<Dipendente> dipendentiAssegnati, Long idTask, TaskState taskState) {
        this.dipendentiAssegnati = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();
        this.idTask = idTask;
        this.taskState = taskState != null ? taskState : TaskState.DAINIZIARE;
    }

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public List<Dipendente> getDipendentiAssegnati() {
        return new ArrayList<>(dipendentiAssegnati);
    }

    public void setDipendentiAssegnati(List<Dipendente> dipendentiAssegnati) {
        this.dipendentiAssegnati = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    // Metodi helper per gestire la lista
    public void assegnaDipendente(Dipendente dipendente) {
        if (dipendente == null) return;

        if (!this.dipendentiAssegnati.contains(dipendente)) {
            this.dipendentiAssegnati.add(dipendente);
            dipendente.getTasks().add(this);
        }
    }

    public void rimuoviDipendente(Dipendente dipendente) {
        if (dipendente == null) return;

        if (this.dipendentiAssegnati.remove(dipendente)) {
            dipendente.getTasks().remove(this);
        }
    }

    public boolean hasDipendente(Dipendente dipendente) {
        return this.dipendentiAssegnati.contains(dipendente);
    }

    public int countDipendenti() {
        return this.dipendentiAssegnati.size();
    }



    public List<Long> getAllDipendentiId() {
        List<Long> idDipendenti = new ArrayList<>();
        for (Dipendente dipendente : dipendentiAssegnati) {
            idDipendenti.add(dipendente.getId()); // Assumendo che Task abbia un metodo getId()
        }
        return idDipendenti;
    }

    @Override
    public String toString() {
        return "Task{" +
                "idTask=" + idTask +
                ", dipendentiAssegnati=" + dipendentiAssegnati +
                ", taskState=" + taskState +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return idTask != null && idTask.equals(task.getIdTask());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
