package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name="dipendente")
public class Dipendente extends Persona {

    private Double stipendio;
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

    public Dipendente(String nome, String cognome) {
        super(nome, cognome);
        this.stipendio = 1800.0;
        this.employeeRole = EmployeeRole.JUNIOR;
        this.tasks = new ArrayList<>();
    }

    public Dipendente(String nome, String cognome, Double stipendio) {
        super(nome, cognome);
        this.stipendio = stipendio;
        this.employeeRole = EmployeeRole.JUNIOR;
        this.tasks = new ArrayList<>();
    }

    public Dipendente(String nome, String cognome, Double stipendio, EmployeeRole employeeRole) {
        super(nome, cognome);
        this.stipendio = stipendio;
        this.employeeRole = employeeRole;
        this.tasks = new ArrayList<>();
    }

    public Double getStipendio() {
        return stipendio;
    }

    public void setStipendio(Double stipendio) {
        this.stipendio = stipendio;
    }

    public EmployeeRole getGrado() {
        return employeeRole;
    }

    public void setGrado(EmployeeRole employeeRole) {
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
            idTask.add(task.getIdTask()); // Assumendo che Task abbia un metodo getId()
        }
        return idTask;
    }

    @Override
    public String toString() {
        String teamId = dipendenteTeam != null ? dipendenteTeam.getIdTeam().toString() : "null";
        return "Dipendente{" + super.toString() +
                "stipendio=" + stipendio +
                ", grado=" + employeeRole +
                ", tasks=" + getAllTaskId() +
                ", dipendenteTeam=" + teamId +
                '}';
    }
}
