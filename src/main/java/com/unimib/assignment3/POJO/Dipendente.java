package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.Grado;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name="dipendente")
public class Dipendente extends Persona {

    private Double stipendio;
    private Grado grado;

    @ManyToMany(mappedBy = "dipendentiAssegnati")
    private List<Task> tasks = new ArrayList<>();

    protected Dipendente() {
        super();
    }

    public Dipendente(String nome, String cognome) {
        super(nome, cognome);
        this.stipendio = 1800.0;
        this.grado = Grado.JUNIOR;
        this.tasks = new ArrayList<>();
    }

    public Dipendente(String nome, String cognome, Double stipendio) {
        super(nome, cognome);
        this.stipendio = stipendio;
        this.grado = Grado.JUNIOR;
        this.tasks = new ArrayList<>();
    }

    public Dipendente(String nome, String cognome, Double stipendio, Grado grado) {
        super(nome, cognome);
        this.stipendio = stipendio;
        this.grado = grado;
        this.tasks = new ArrayList<>();
    }

    public Double getStipendio() {
        return stipendio;
    }

    public void setStipendio(Double stipendio) {
        this.stipendio = stipendio;
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
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
        return "Dipendente{" + super.toString() +
                "stipendio=" + stipendio +
                ", grado=" + grado +
                ", tasks=" + getAllTaskId() +
                '}';
    }
}
