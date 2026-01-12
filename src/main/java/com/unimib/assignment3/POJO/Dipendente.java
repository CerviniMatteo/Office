package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="dipendente")
public class Dipendente extends Persona{

    private double monthlySalary;
    private EmployeeRole employeeRole;

    @ManyToMany(mappedBy = "dipendentiAssegnati")
    private List<Task> tasks = new ArrayList<>();

    // prototipo di relazione ManyToOne con Team
    @ManyToOne
    @JoinColumn(name = "dipendenteTeam")
    private Team dipendenteTeam;

    public Team getDipendenteTeam() {
        return dipendenteTeam;
    }

    public void setDipendenteTeam(Team team) {
        this.dipendenteTeam = team;
    }

    protected Dipendente() {
        super();
    }

    public Dipendente(String name, String surname) {
        super(name, surname);
        setEmployeeRole(EmployeeRole.JUNIOR);
        setMonthlySalary(EmployeeRole.JUNIOR.getMonthlySalary());
    }

    public Dipendente(String name, String surname, EmployeeRole employeeRole) {
        super(name, surname);
        setEmployeeRole(employeeRole);
        setMonthlySalary(employeeRole.getMonthlySalary());
    }

    public Dipendente(String name, String surname, double monthlySalary, EmployeeRole employeeRole) {
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
        String teamId = dipendenteTeam != null ? dipendenteTeam.getIdTeam().toString() : "null";
        return "Dipendente{" + super.toString() +
                "stipendio=" + monthlySalary +
                ", grado=" + employeeRole +
                ", tasks=" + getAllTaskId() +
                ", dipendenteTeam=" + teamId +
                '}';
    }
}
