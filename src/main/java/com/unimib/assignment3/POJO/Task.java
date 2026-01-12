package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.TaskState;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name="task")
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_state")
    private TaskState taskState;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToMany
    @JoinTable(
            name = "task_dipendenti",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dipendente_id")
    )
    private List<Dipendente> dipendentiAssegnati = new ArrayList<>();

    // funziona per la relazione ManyToOne con Team per adesso
    // ricordati di wrapparlo nel service quando lo usi e di conseguenza nel facade
    @ManyToOne
    @JoinColumn(name = "taskTeam")
    private Team taskTeam;

    public Team getTaskTeam() {
        return taskTeam;
    }
    public void setTaskTeam(Team team) {
        this.taskTeam = team;
    }

    public Task() {
        this.taskState = TaskState.STARTED;

    }

    public Task(TaskState taskState) {
        this.taskState = taskState != null ? taskState : TaskState.STARTED;
    }

    public Task(List<Dipendente> dipendentiAssegnati,  TaskState taskState, LocalDate startDate, LocalDate endDate) {
        this.dipendentiAssegnati = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();
        this.taskState = taskState != null ? taskState : TaskState.STARTED;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long idTask) {
        this.taskId = idTask;
    }

    public List<Dipendente> getAssignedEmployees() {
        return new ArrayList<>(dipendentiAssegnati);
    }

    public void setAssignedEmployees(List<Dipendente> dipendentiAssegnati) {
        this.dipendentiAssegnati = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public void assignEmployee(Dipendente dipendente) {
        if (dipendente == null) return;

        if (!this.dipendentiAssegnati.contains(dipendente)) {
            this.dipendentiAssegnati.add(dipendente);
            dipendente.getTasks().add(this);
        }
    }

    public void removeEmployee(Dipendente dipendente) {
        if (dipendente == null) return;

        if (this.dipendentiAssegnati.remove(dipendente)) {
            dipendente.getTasks().remove(this);
        }
    }

    public boolean hasEmployee(Dipendente dipendente) {
        return this.dipendentiAssegnati.contains(dipendente);
    }

    public int countEmployees() {
        return this.dipendentiAssegnati.size();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate != null && this.endDate != null && startDate.isAfter(this.endDate)) {
            throw new IllegalArgumentException("La data di inizio non può essere successiva alla data di fine.");
        }
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate != null && this.startDate != null && endDate.isBefore(this.startDate)) {
            throw new IllegalArgumentException("La data di fine non può essere precedente alla data di inizio.");
        }
        this.endDate = endDate;
    }

    public List<Long> getAllEmployeesId() {
        List<Long> idDipendenti = new ArrayList<>();
        for (Dipendente dipendente : dipendentiAssegnati) {
            idDipendenti.add(dipendente.getId()); // Assumendo che Task abbia un metodo getId()
        }
        return idDipendenti;
    }

    @Override
    public String toString() {
        String idTeam = taskTeam != null ? taskTeam.getIdTeam().toString() : "null";
        return "Task{" +
                "idTask=" + taskId +
                ", dipendentiAssegnati=" + getAllEmployeesId() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", taskState=" + taskState +
                ", taskTeam=" + idTeam +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return taskId != null && taskId.equals(task.getTaskId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
