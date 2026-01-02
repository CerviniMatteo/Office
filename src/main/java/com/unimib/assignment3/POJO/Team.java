package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name="team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTeam;

    @ManyToMany
    @JoinTable(name = "team_dipendente",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "dipendente_id"))
    private List<Dipendente> dipendenti = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "team_supervisionato")
    private Supervisore supervisore;

    @OneToMany
    @JoinTable(
            name = "team_tasks",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<Task> tasks;

    protected Team() {
    }

    public Team(Supervisore supervisore) {
        this.supervisore = supervisore;
    }
    public Team(List<Dipendente> dipendenti) {
        this.dipendenti = dipendenti;
    }
    public Team(List<Dipendente> dipendenti, Supervisore supervisore, List<Task> tasks) {
        this.dipendenti = dipendenti;
        this.supervisore = supervisore;
        this.tasks = tasks;
    }

    public Long getIdTeam() {
        return idTeam;
    }
    // non serve il setIdTeam in quanto l'id viene generato automaticamente

    public List<Dipendente> getDipendenti() {
        return dipendenti;
    }
    public void setDipendenti(List<Dipendente> dipendenti) {
        this.dipendenti = dipendenti;
    }
    public void addDipendente(Dipendente dipendente) {
        this.dipendenti.add(dipendente);
    }
    public void removeDipendente(Dipendente dipendente) {
        this.dipendenti.remove(dipendente);
    }

    public Supervisore getSupervisore() {
        return supervisore;
    }
    public void setSupervisore(Supervisore supervisore) {
        this.supervisore = supervisore;
    }

    public List<Task> getTasksTeam() {
        return tasks;
    }
    public void setTasksTeam(List<Task> tasks) {
        this.tasks = tasks;
    }
    public void addTaskTeam(Task task, String stato) {
        this.tasks.add(task);
    }
    public void removeTaskTeam(Task task) {
        this.tasks.remove(task);
    }

    @Override
    public String toString() {
        return "Team{" +
                "idTeam=" + idTeam +
                ", dipendenti=" + dipendenti +
                ", supervisore=" + supervisore +
                ", tasks=" + tasks +
                '}';
    }
}
