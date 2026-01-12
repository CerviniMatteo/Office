package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name="team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTeam;

    // orphane removal not necessary as the deletion of a team does not imply
    // the deletion of the associated employees
    @OneToMany(mappedBy = "dipendenteTeam", cascade = CascadeType.MERGE)
    private List<Dipendente> dipendenti = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "supervisore")
    private Supervisore supervisore;

    // orphane removal not necessary as the deletion of a team does not imply
    // the deletion of the associated tasks
    @OneToMany(mappedBy = "taskTeam", cascade = CascadeType.MERGE)
    private List<Task> tasks = new ArrayList<>();

    protected Team() {
    }

    public Team(Supervisore supervisore) {
        // da discutere
        supervisore.addTeamsSupervisionato(this);
    }
    public Team(List<Dipendente> dipendenti, Supervisore supervisore) {
        setDipendenti(dipendenti);
        // da discutere
        supervisore.addTeamsSupervisionato(this);
    }
    public Team(List<Dipendente> dipendenti, Supervisore supervisore, List<Task> tasks) {
        setDipendenti(dipendenti);
        // da discutere
        supervisore.addTeamsSupervisionato(this);
        setTasks(tasks);
    }

    public Long getIdTeam() {
        return idTeam;
    }
    // non serve il setIdTeam in quanto l'id viene generato automaticamente

    public List<Dipendente> getDipendenti() {
        return dipendenti;
    }
    // per motivi di progettazione la set della lista di dipendenti viene usato solo all'interno della
    // classe per gestire la relazione bidirezionale, al di fuori della classe per aggiungere una lista di
    // dipendenti si usa un ciclo con addDipendente --> nella doc.
    private void setDipendenti(List<Dipendente> dipendenti) {
        removeAllDipendenti();
        for(Dipendente dipendente : dipendenti) {
            dipendente.setDipendenteTeam(this);
        }
        this.dipendenti = dipendenti;
    }
    private void setDipendenti(Dipendente dipendente) {
        this.dipendenti.add(dipendente);
    }
    public void addDipendente(Dipendente dipendente) {
        if(!dipendenti.contains(dipendente)){
            setDipendenti(dipendente);
            dipendente.setDipendenteTeam(this);
        }
    }
    public void removeAllDipendenti() {
        for(Dipendente dipendente : dipendenti) {
            dipendente.setDipendenteTeam(null);
        }
        dipendenti.clear();
    }
    public void removeDipendente(Dipendente dipendente) {
        if(dipendenti.remove(dipendente)){
            dipendente.setDipendenteTeam(null);
        }
    }

    public Supervisore getSupervisore() {
        return supervisore;
    }
    public void setSupervisore(Supervisore supervisore) {
        this.supervisore = supervisore;
    }

    public List<Task> getTasks() {
        return tasks;
    }
    private void setTasks(List<Task> tasks) {
        removeAllTasks();
        for(Task task : tasks) {
            task.setTaskTeam(this);
        }
        this.tasks = tasks;
    }
    private void setTasks(Task task) {
        this.tasks.add(task);
    }
    public void addTask(Task task) {
        if(!tasks.contains(task)){
            setTasks(task);
            task.setTaskTeam(this);
        }
    }
    public void removeAllTasks() {
        for(Task task : tasks) {
            task.setTaskTeam(null);
        }
        tasks.clear();
    }
    public void removeTask(Task task) {
        if(tasks.remove(task)){
            task.setTaskTeam(null);
        }
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
