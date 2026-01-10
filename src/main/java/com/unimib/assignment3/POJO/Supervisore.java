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
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Team> teamSupervisionati = new ArrayList<>();

    protected Supervisore() {
        super();
    }

    public Supervisore(String name, String surname) {
        super(name, surname, EmployeeRole.SW_ARCHITECT);
    }

    public Supervisore(String name, String surname, Double monthlySalary, EmployeeRole employeeRole) {
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

    public List<Team> getTeamSupervisionati() {
        return teamSupervisionati;
    }

    public void setSupervisoriSupervisionati(List<Supervisore> subordinates) {
        this.supervisoriSupervisionati = subordinates;
    }

    public void setSupervisoriSupervisionati(Supervisore subordiante) {
        this.supervisoriSupervisionati.add(subordiante);
    }

    public void setTeamSupervisionati(List<Team> supervisedTeams) {
        this.teamSupervisionati = supervisedTeams;
    }

    public void setTeamSupervisionati(Team supervisedTeams) {
        this.teamSupervisionati.add(supervisedTeams);
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

    private void checkRole(EmployeeRole employeeRole) {
        if(employeeRole.compareTo(EmployeeRole.SW_ARCHITECT) < 0) {
            throw new IllegalArgumentException(SupervisorConstants.SUPERVISOR_AT_LEAST_SW_ARCHITECT);
        }
    }

    @Override
    public String toString() {
        return "Supervisor{" + super.toString() +
                ", subordinates=" + supervisoriSupervisionati.stream().map(team -> team.getId().toString()).collect(Collectors.joining(", ")) +
                ", supervisedTeams=" + teamSupervisionati.stream().map(team -> team.getIdTeam().toString()).collect(Collectors.joining(", ")) +
                '}';
    }
}
