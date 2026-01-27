package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.WorkerRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Supervisor, which is a specialized type of Employee capable of supervising other employees
 * and managing teams. This class supports hierarchical relationships between supervisors and their subordinates.
 *
 * <p>
 * The class maintains bidirectional associations with:
 * <ul>
 *     <li>Other Supervisors (subordinates)</li>
 *     <li>Teams supervised by this Supervisor</li>
 * </ul>
 * </p>
 *
 * <p>
 * Entities are persisted using JPA annotations. Cascade rules ensure that subordinates and teams are
 * managed appropriately when a Supervisor entity is modified.
 * </p>
 *
 * @see Employee
 * @see Team
 */
@Entity(name = "supervisor")
public class Supervisor extends Worker {

    /**
     * The supervisor of this Supervisor.
     */
    @ManyToOne
    @JoinColumn(name = "parent_supervisor_id")
    private Supervisor supervisor;

    /**
     * List of subordinates reporting directly to this Supervisor.
     */
    @OneToMany(
            mappedBy = "supervisor",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<Supervisor> subordinates = new ArrayList<>();

    /**
     * List of teams supervised by this Supervisor.
     */
    @OneToMany(
            mappedBy = "supervisor",
            cascade = CascadeType.MERGE
    )
    private List<Team> supervisedTeams;

    /**
     * Protected no-argument constructor required by JPA.
     */
    protected Supervisor() {
        super();
        supervisedTeams = new ArrayList<>();
    }

    /**
     * Creates a Supervisor with default role {@link WorkerRole#SW_ARCHITECT}.
     *
     * @param name    the supervisor's first name
     * @param surname the supervisor's last name
     */
    public Supervisor(String name, String surname) {
        super(name, surname, WorkerRole.SW_ARCHITECT);
        supervisedTeams = new ArrayList<>();
    }

    /**
     * Creates a Supervisor with a specified role.
     *
     * @param name         the supervisor's first name
     * @param surname      the supervisor's last name
     * @param workerRole the role of the supervisor
     */
    public Supervisor(String name, String surname, WorkerRole workerRole) {
        super(name, surname, workerRole);
        supervisedTeams = new ArrayList<>();
    }

    /**
     * Creates a Supervisor with full details including hierarchy and supervised teams.
     *
     * @param name            the supervisor's first name
     * @param surname         the supervisor's last name
     * @param workerRole    the role of the supervisor
     * @param supervisor      the supervisor of this supervisor
     * @param subordinates    list of subordinates
     */
    public Supervisor(String name, String surname, WorkerRole workerRole, Supervisor supervisor, List<Supervisor> subordinates) {
        super(name, surname, workerRole);
        setSupervisor(supervisor);
        setSubordinates(subordinates);
        supervisedTeams = new ArrayList<>();
    }

    /**
     * Creates a Supervisor with specified salary.
     *
     * @param name          the supervisor's first name
     * @param surname       the supervisor's last name
     * @param monthlySalary the monthly salary of the supervisor
     * @param workerRole  the role of the supervisor
     */
    public Supervisor(String name, String surname, double monthlySalary, WorkerRole workerRole) {
        super(name, surname, monthlySalary, workerRole);
        supervisedTeams = new ArrayList<>();
    }

    /**
     * Creates a Supervisor with full details including salary, hierarchy, and supervised teams.
     *
     * @param name            the supervisor's first name
     * @param surname         the supervisor's last name
     * @param monthlySalary   the monthly salary of the supervisor
     * @param workerRole    the role of the supervisor
     * @param supervisor      the supervisor of this supervisor
     * @param subordinates    list of subordinates
     */
    public Supervisor(String name, String surname, double monthlySalary, WorkerRole workerRole, Supervisor supervisor, List<Supervisor> subordinates) {
        super(name, surname, monthlySalary, workerRole);
        setSupervisor(supervisor);
        setSubordinates(subordinates);
        supervisedTeams = new ArrayList<>();
    }

    @Override
    public void setWorkerRole(WorkerRole workerRole) {
        super.setWorkerRole(workerRole);
    }

    @Override
    public void setMonthlySalary(double monthlySalary) {
        super.setMonthlySalary(monthlySalary);
    }

    /**
     * Returns the supervisor of this supervisor.
     *
     * @return the supervisor
     */
    public Supervisor getSupervisor() {
        return supervisor;
    }

    /**
     * Sets the supervisor of this supervisor.
     *
     * @param supervisor the supervisor to set
     */
    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    /**
     * Returns the list of subordinates.
     *
     * @return list of subordinates
     */
    public List<Supervisor> getSubordinates() {
        return subordinates;
    }

    /**
     * Sets the list of subordinates, replacing existing ones.
     *
     * @param subordinates the new list of subordinates
     */
    public void setSubordinates(List<Supervisor> subordinates) {
        this.subordinates = subordinates;
    }

    /**
     * Adds a single subordinate to the list.
     *
     * @param subordinate the subordinate to add
     */
    public void setSubordinates(Supervisor subordinate) {
        this.subordinates.add(subordinate);
    }

    /**
     * Returns the list of teams supervised by this supervisor.
     *
     * @return list of supervised teams
     */
    public List<Team> getSupervisedTeams() {
        return supervisedTeams;
    }

    /**
     * Adds a single team to the list of supervised teams.
     *
     * @param team the team to add
     */
    private void setSupervisedTeams(Team team) {
        this.supervisedTeams.add(team);
    }

    /**
     * Adds a subordinate and maintains bidirectional link.
     *
     * @param subordinate the subordinate to add
     */
    public void addSubordinate(Supervisor subordinate) {
        if (subordinate == null) return;
        if (!this.subordinates.contains(subordinate)) {
            setSubordinates(subordinate);
            subordinate.setSupervisor(this);
        }
    }

    /**
     * Removes a subordinate and clears bidirectional link.
     *
     * @param subordinate the subordinate to remove
     */
    public void removeSubordinate(Supervisor subordinate) {
        if (subordinate == null) return;
        if (this.subordinates.remove(subordinate)) {
            subordinate.setSupervisor(null);
        }
    }

    /**
     * Adds a team to the supervised teams and maintains bidirectional link.
     *
     * @param team the team to add
     */
    public void addSupervisedTeam(Team team) {
        if(!supervisedTeams.contains(team)){
            setSupervisedTeams(team);
            team.setSupervisor(this);
        }
    }

    /**
     * Removes all supervised teams and clears their supervisor reference.
     */
    public void removeAllSupervisedTeams() {
        for(Team team : supervisedTeams) {
            team.setSupervisor(null);
        }
        supervisedTeams.clear();
    }

    /**
     * Removes a specific team from the supervised teams.
     *
     * @param team the team to remove
     */
    public void removeSupervisedTeam(Team team) {
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
