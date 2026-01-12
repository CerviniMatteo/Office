package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="employee")
public class Employee extends Person {

    private double monthlySalary;
    private EmployeeRole employeeRole;

    @ManyToMany(mappedBy = "assignedEmployees")
    private List<Task> tasks = new ArrayList<>();

    // prototipo di relazione ManyToOne con Team
    @ManyToOne
    @JoinColumn(name = "employeeTeam")
    private Team employeeTeam;

    public Team getEmployeeTeam() {
        return employeeTeam;
    }

    public void setEmployeeTeam(Team team) {
        this.employeeTeam = team;
    }

    protected Employee() {
        super();
    }

    public Employee(String name, String surname) {
        super(name, surname);
        setEmployeeRole(EmployeeRole.JUNIOR);
        setMonthlySalary(EmployeeRole.JUNIOR.getMonthlySalary());
    }

    public Employee(String name, String surname, EmployeeRole employeeRole) {
        super(name, surname);
        setEmployeeRole(employeeRole);
        setMonthlySalary(employeeRole.getMonthlySalary());
    }

    public Employee(String name, String surname, double monthlySalary, EmployeeRole employeeRole) {
        super(name, surname);
        setMonthlySalary(monthlySalary);
        setEmployeeRole(employeeRole);
    }

    public Double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(Double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }


    private List<Long> getAllTaskId() {
        List<Long> idTask = new ArrayList<>();
        for (Task task : tasks) {
            idTask.add(task.getTaskId());
        }
        return idTask;
    }

    @Override
    public String toString() {
        String teamId = employeeTeam != null ? employeeTeam.getTeamId().toString() : "null";
        return "Dipendente{" + super.toString() +
                "stipendio=" + monthlySalary +
                ", grado=" + employeeRole +
                ", tasks=" + getAllTaskId() +
                ", dipendenteTeam=" + teamId +
                '}';
    }
}
