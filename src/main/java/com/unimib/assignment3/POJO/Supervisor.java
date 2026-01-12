package com.unimib.assignment3.POJO;

import com.unimib.assignment3.constants.SupervisorConstants;
import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "supervisor")
public class Supervisor extends Employee {

    @ManyToOne
    @JoinColumn(name = "supervisor")
    private Supervisor supervisor;

    @OneToMany(
            mappedBy = "supervisor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Supervisor> subordinates = new ArrayList<>();

    @OneToMany(
            mappedBy = "supervisor",
            cascade = CascadeType.MERGE,
            orphanRemoval = true
    )
    private List<Team> supervisedTeams = new ArrayList<>();

    protected Supervisor() {
        super();
    }

    public Supervisor(String name, String surname) {
        super(name, surname, EmployeeRole.SW_ARCHITECT);
    }

    public Supervisor(String name, String surname, EmployeeRole employeeRole) {
        super(name, surname, employeeRole);
    }

    public Supervisor(String name, String surname, double monthlySalary, EmployeeRole employeeRole) {
        super(name, surname, monthlySalary, employeeRole);
    }

    @Override
    public void setEmployeeRole(EmployeeRole employeeRole) {
        checkRole(employeeRole);
        super.setEmployeeRole(employeeRole);
    }

    protected void checkRole(EmployeeRole employeeRole) {
        if(employeeRole.compareTo(EmployeeRole.SW_ARCHITECT) < 0) {
            throw new IllegalArgumentException(SupervisorConstants.SUPERVISOR_AT_LEAST_SW_ARCHITECT);
        }
    }

    public Supervisor getSupervisore() {
        return supervisor;
    }

    public void setSupervisore(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public List<Supervisor> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<Supervisor> subordinates) {
        this.subordinates = subordinates;
    }

    public void setSupervisoriSupervisionati(Supervisor subordinate) {
        this.subordinates.add(subordinate);
    }

    //TODO ho cambiato il setter di team supervisionato per gestire la relazione bidirezionale
    // ricordati di aggiornare anche nel service e test
    public List<Team> getSupervisedTeams() {
        return supervisedTeams;
    }

    private void setSupervisedTeams(Team team) {
        this.supervisedTeams.add(team);
    }

    private void setTeamsSupervisionato(List<Team> teams) {
        removeAllTeamsSupervisionato();
        for(Team team : teams) {
            team.setSupervisor(this);
        }
        this.supervisedTeams = teams;
    }

    public void addSubordinate(Supervisor subordinate) {
        if (subordinate == null) return;
        if (!this.subordinates.contains(subordinate)) {
            setSupervisoriSupervisionati(subordinate);
            subordinate.setSupervisore(this); // maintain bidirectional link
        }
    }

    public void removeSubordinate(Supervisor subordinate) {
        if (subordinate == null) return;
        if (this.subordinates.remove(subordinate)) {
            subordinate.setSupervisore(null); // remove bidirectional link
        }
    }

    public void addTeamsSupervisionato(Team team) {
        if(!supervisedTeams.contains(team)){
            setSupervisedTeams(team);
            team.setSupervisor(this);
        }
    }

    public void removeAllTeamsSupervisionato() {
        for(Team team : supervisedTeams) {
            team.setSupervisor(null);
        }
        supervisedTeams.clear();
    }

    public void removeTeamSupervisionato(Team team) {
        if (supervisedTeams.remove(team)) {
            team.setSupervisor(null);
        }
    }

    @Override
    public String toString() {
        return "Supervisor{" + super.toString() +
                ", subordinates=" + subordinates +
                ", supervisedTeams=" + supervisedTeams.stream().map(team -> team.getTeamId().toString()).collect(Collectors.joining(", ")) +
                '}';
    }
}
