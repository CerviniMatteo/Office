package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.TaskState;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.unimib.assignment3.constants.*;

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
            name = "assignedEmployees",
            joinColumns = @JoinColumn(name = "taskId"),
            inverseJoinColumns = @JoinColumn(name = "employeeId")
    )
    private List<Employee> assignedEmployees = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "teamTask")
    private Team teamTask;


    protected Task() {
        this.taskState = TaskState.STARTED;

    }

    public Task(TaskState taskState) {
        this.taskState = taskState != null ? taskState : TaskState.STARTED;
    }

    public Task(List<Employee> assignedEmployees, TaskState taskState, LocalDate startDate, LocalDate endDate) {
        this.assignedEmployees = assignedEmployees != null ? assignedEmployees : new ArrayList<>();
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

    public List<Employee> getAssignedEmployees() {
        return new ArrayList<>(assignedEmployees);
    }

    public void setAssignedEmployees(List<Employee> dipendentiAssegnati) {
        this.assignedEmployees = dipendentiAssegnati != null ? dipendentiAssegnati : new ArrayList<>();
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public void assignEmployee(Employee employee) {
        if (employee == null) return;

        if (!this.assignedEmployees.contains(employee)) {
            this.assignedEmployees.add(employee);
            employee.getTasks().add(this);
        }
    }

    public void removeEmployee(Employee employee) {
        if (employee == null) return;

        if (this.assignedEmployees.remove(employee)) {
            employee.getTasks().remove(this);
        }
    }

    public boolean hasEmployee(Employee employee) {
        return this.assignedEmployees.contains(employee);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate != null && this.endDate != null && startDate.isAfter(this.endDate)) {
            throw new IllegalArgumentException(TaskConstants.START_DATE_AFTER_END);
        }
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate != null && this.startDate != null && endDate.isBefore(this.startDate)) {
            throw new IllegalArgumentException(TaskConstants.END_DATE_BEFORE_START);
        }
        this.endDate = endDate;
    }

    public List<Long> getAllEmployeesId() {
        List<Long> idDipendenti = new ArrayList<>();
        for (Employee employee : assignedEmployees) {
            idDipendenti.add(employee.getPersonId());
        }
        return idDipendenti;
    }

    public Team getTeamTask() {
        return teamTask;
    }
    public void setTeamTask(Team team) {
        this.teamTask = team;
    }

    @Override
    public String toString() {
        String idTeam = teamTask != null ? teamTask.getTeamId().toString() : "null";
        return "Task{" +
                "task id=" + taskId +
                ", assigned employees=" + getAllEmployeesId() +
                ", start date=" + startDate +
                ", end date=" + endDate +
                ", task state=" + taskState +
                ", team=" + idTeam +
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
