package com.unimib.assignment3.POJO;

import com.unimib.assignment3.constants.SupervisorConstants;
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

    public Supervisore(String name, String surname) {
        super(name, surname, EmployeeRole.SW_ARCHITECT);
    }

    public Supervisore(String name, String surname, EmployeeRole employeeRole) {
        super(name, surname, employeeRole);
    }

    public Supervisore(String name, String surname, double monthlySalary, EmployeeRole employeeRole) {
        super(name, surname, monthlySalary, employeeRole);
    }

    @Override
    public void setEmployeeRole(EmployeeRole employeeRole) {
        checkRole(employeeRole);
        super.setEmployeeRole(employeeRole);
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

    public void setSupervisoriSupervisionati(List<Supervisore> subordinates) {
        this.supervisoriSupervisionati = subordinates;
    }

    public void setSupervisoriSupervisionati(Supervisore subordinate) {
        this.supervisoriSupervisionati.add(subordinate);
    }

    // ho cambiato il setter di team supervisionato per gestire la relazione bidirezionale
    // ricordati di aggiornare anche nel service e test
    public List<Team> getTeamsSupervisionato() {
        return teamsSupervisionato;
    }

    private void setTeamsSupervisionato(Team team) {
        this.teamsSupervisionato.add(team);
    }

    private void setTeamsSupervisionato(List<Team> teams) {
        removeAllTeamsSupervisionato();
        for(Team team : teams) {
            team.setSupervisore(this);
        }
        this.teamsSupervisionato = teams;
    }

    public void addSubordinate(Supervisore subordinate) {
        if (subordinate == null) return;
        if (!this.supervisoriSupervisionati.contains(subordinate)) {
            setSupervisoriSupervisionati(subordinate);
            subordinate.setSupervisore(this); // maintain bidirectional link
        }
    }

    public void removeSubordinate(Supervisore subordinate) {
        if (subordinate == null) return;
        if (this.supervisoriSupervisionati.remove(subordinate)) {
            subordinate.setSupervisore(null); // remove bidirectional link
        }
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
