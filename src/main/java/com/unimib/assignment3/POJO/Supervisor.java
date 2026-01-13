package com.unimib.assignment3.POJO;

import com.unimib.assignment3.constants.SupervisorConstants;
import static com.unimib.assignment3.constants.CommonConstants.*;
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

    @Override
    public void setMonthlySalary(double monthlySalary) {
        if(monthlySalary < EmployeeRole.SW_ARCHITECT.getMonthlySalary()){
            throw new IllegalArgumentException(SALARY_MUST_BE_POSITIVE);
        } else {
            super.setMonthlySalary(monthlySalary);

        }
    }

    protected void checkRole(EmployeeRole employeeRole) {
        if(employeeRole.compareTo(EmployeeRole.SW_ARCHITECT) < 0) {
            throw new IllegalArgumentException(SupervisorConstants.SUPERVISOR_AT_LEAST_SW_ARCHITECT);
        }
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public List<Supervisor> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<Supervisor> subordinates) {
        this.subordinates = subordinates;
    }

    public void setSubordinates(Supervisor subordinate) {
        this.subordinates.add(subordinate);
    }

    public List<Team> getSupervisedTeams() {
        return supervisedTeams;
    }

    private void setSupervisedTeams(Team team) {
        this.supervisedTeams.add(team);
    }

    private void setSupervisedTeams(List<Team> teams) {
        removeAllSupervisedTeams();
        for(Team team : teams) {
            team.setSupervisor(this);
        }
        this.supervisedTeams = teams;
    }

    public void addSubordinate(Supervisor subordinate) {
        if (subordinate == null) return;
        if (!this.subordinates.contains(subordinate)) {
            setSubordinates(subordinate);
            subordinate.setSupervisor(this); // maintain bidirectional link
        }
    }

    public void removeSubordinate(Supervisor subordinate) {
        if (subordinate == null) return;
        if (this.subordinates.remove(subordinate)) {
            subordinate.setSupervisor(null); // remove bidirectional link
        }
    }

    public void addSupervisedTeams(Team team) {
        if(!supervisedTeams.contains(team)){
            setSupervisedTeams(team);
            team.setSupervisor(this);
        }
    }

    public void removeAllSupervisedTeams() {
        for(Team team : supervisedTeams) {
            team.setSupervisor(null);
        }
        supervisedTeams.clear();
    }

    public void removeSupervisedTeams(Team team) {
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
