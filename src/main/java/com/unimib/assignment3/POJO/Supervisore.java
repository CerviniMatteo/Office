// java
package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "supervisore")
public class Supervisore extends Dipendente {

    @ManyToOne
    @JoinColumn(name = "supervisore")
    private Supervisore supervisore;

    @OneToMany(
            mappedBy = "supervisore",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Supervisore> supervisoriSupervisionati = new ArrayList<>();

    @OneToMany(
            mappedBy = "supervisore",
            cascade = CascadeType.MERGE,
            orphanRemoval = true
    )
    private List<Team> teamsSupervisionato = new ArrayList<>();

    protected Supervisore() {
        super();
    }

    public Supervisore(String nome, String cognome) {
        super(nome, cognome, 2800.0, EmployeeRole.SW_ARCHITECT);
    }

    public Supervisore(String nome, String cognome, Double stipendio, EmployeeRole employeeRole) {
        super(nome, cognome, stipendio, employeeRole);
    }

    public Supervisore getSupervisore() {
        return supervisore;
    }

    public void setSupervisore(Supervisore supervisore) {
        this.supervisore = supervisore;
    }

    public List<Supervisore> getSupervisoriSupervisionati() {
        return supervisoriSupervisionati;
    }

    public void setSupervisoriSupervisionati(List<Supervisore> supervisoriSupervisionati) {
        this.supervisoriSupervisionati = supervisoriSupervisionati;
    }

    public void setSupervisoreSupervisionato(Supervisore supervisoreSupervisionato) {
        this.supervisoriSupervisionati.add(supervisoreSupervisionato);
    }

    // ho cambiato il setter di team supervisionato per gestire la relazione bidirezionale
    // ricordati di aggiornare anche nel service e test
    public List<Team> getTeamsSupervisionato() {
        return teamsSupervisionato;
    }

    private void setTeamsSupervisionato(List<Team> teams) {
        removeAllTeamsSupervisionato();
        for(Team team : teams) {
            team.setSupervisore(this);
        }
        this.teamsSupervisionato = teams;
    }
    private void setTeamsSupervisionato(Team team) {
        this.teamsSupervisionato.add(team);
    }
    public void addTeamsSupervisionato(Team team) {
        if(!teamsSupervisionato.contains(team)){
            setTeamsSupervisionato(team);
            team.setSupervisore(this);
        }
    }
    public void removeAllTeamsSupervisionato() {
        for(Team team : teamsSupervisionato) {
            team.setSupervisore(null);
        }
        teamsSupervisionato.clear();
    }
    public void removeTeamSupervisionato(Team team) {
        if(teamsSupervisionato.remove(team)){
            team.setSupervisore(null);
        }
    }

    @Override
    public String toString() {
        return "Supervisore{" + super.toString() +
                ", supervisoriSupervisionati=" + supervisoriSupervisionati +
                ", teamSupervisionato=" + teamsSupervisionato.stream().map(team -> team.getIdTeam().toString()).collect(Collectors.joining(", ")) +
                '}';
    }
}
