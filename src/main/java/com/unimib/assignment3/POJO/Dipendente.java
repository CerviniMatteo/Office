package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.unimib.assignment3.constants.SupervisorConstants.SUPERVISOR_AT_LEAST_SW_ARCHITECT;

@Entity(name="dipendente")
public class Dipendente extends Persona{

    private static final Logger log = LoggerFactory.getLogger(Dipendente.class);
    private Double monthlySalary;
    private EmployeeRole employeeRole;

    @ManyToMany(mappedBy = "dipendentiAssegnati")
    private List<Task> tasks = new ArrayList<>();

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

    public Dipendente(String name, String surname, Double monthlySalary, EmployeeRole employeeRole) {
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
            idTask.add(task.getIdTask());
        }
        return idTask;
    }

    @Override
    public String toString() {
        return "Employee{" + super.toString() +
                "monthlySalaray=" + monthlySalary +
                ", employeeRole=" + employeeRole +
                ", tasks=" + getAllTaskId() +
                '}';
    }
}
