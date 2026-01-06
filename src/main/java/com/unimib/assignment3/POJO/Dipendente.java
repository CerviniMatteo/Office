package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name="dipendente")
public class Dipendente extends Persona{

    private Double monthlySalary;
    private EmployeeRole employeeRole;

    @ManyToMany(mappedBy = "dipendentiAssegnati")
    private List<Task> tasks = new ArrayList<>();

    protected Dipendente() {
        super();
    }

    public Dipendente(String nome, String cognome) {
        super(nome, cognome);
        this.employeeRole = EmployeeRole.JUNIOR;
        this.monthlySalary = EmployeeRole.JUNIOR.getMonthlySalary();
    }

    public Dipendente(String nome, String cognome, EmployeeRole employeeRole) {
        super(nome, cognome);
        this.employeeRole = employeeRole;
        this.monthlySalary = employeeRole.getMonthlySalary();
    }

    public Dipendente(String nome, String cognome, Double monthlySalary, EmployeeRole employeeRole) {
        super(nome, cognome);
        this.employeeRole = employeeRole;
        this.monthlySalary = monthlySalary;
    }

    public Double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(Double stipendio) {
        this.monthlySalary = stipendio;
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


    public List<Long> getAllTaskId() {
        List<Long> idTask = new ArrayList<>();
        for (Task task : tasks) {
            idTask.add(task.getIdTask());
        }
        return idTask;
    }

    @Override
    public String toString() {
        return "Dipendente{" + super.toString() +
                "stipendio=" + monthlySalary +
                ", grado=" + employeeRole +
                ", tasks=" + getAllTaskId() +
                '}';
    }
}
