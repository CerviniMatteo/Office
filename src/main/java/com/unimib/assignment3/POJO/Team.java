package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.TeamTaskState;
import jakarta.persistence.*;

import java.util.List;
import java.util.HashMap;

@Entity(name="team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idTeam;

    @OneToMany
    @JoinTable(
            name = "team_dipendenti",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "dipendente_id")
    )
    private List<Dipendente> dipendenti;

    @ManyToOne
    @JoinColumn(name = "supervisore_id")
    private Supervisore supervisore;

    @OneToMany
    @JoinTable(
            name = "team_tasks",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private HashMap<Task, TeamTaskState> tasks = new HashMap<>();

    protected Team() {
    }

    public Team(Supervisore supervisore) {
        this.supervisore = supervisore;
    }
    public Team(List<Dipendente> dipendenti, Supervisore supervisore) {
        this.dipendenti = dipendenti;
        this.supervisore = supervisore;
    }
    public Team(List<Dipendente> dipendenti, Supervisore supervisore, HashMap<Task, TeamTaskState> tasksTeam) {
        this.dipendenti = dipendenti;
        this.supervisore = supervisore;
        this.tasks = tasksTeam;
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

    public HashMap<Task, TeamTaskState> getTasksTeam() {
        return tasks;
    }
    public void setTasksTeam(HashMap<Task, TeamTaskState> tasksTeam) {
        this.tasks = tasksTeam;
    }
    public void addTaskTeam(Task task, TeamTaskState stato) {
        this.tasks.put(task, stato);
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
