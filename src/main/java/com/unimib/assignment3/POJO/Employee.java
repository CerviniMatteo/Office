package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.EmployeeRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an employee in the system.
 * <p>
 * Extends {@link Person} and adds employee-specific attributes such as:
 * - monthly salary
 * - employee role
 * - assigned tasks
 * - team assignment
 * <p>
 * Supports JPA entity mapping with:
 * - Many-to-many relationship with {@link Task} (assigned tasks)
 * - Many-to-one relationship with {@link Team} (employee's team)
 */
@Entity(name = "employee")
public class Employee extends Person {

    /** Monthly salary of the employee. */
    private double monthlySalary;

    /** Role of the employee. */
    private EmployeeRole employeeRole;

    /**
     * List of tasks assigned to this employee.
     * Many-to-many relationship mapped by "assignedEmployees" in {@link Task}.
     */
    @ManyToMany(mappedBy = "assignedEmployees",
                cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Task> tasks = new ArrayList<>();

    /**
     * The team to which this employee belongs.
     * Many-to-one relationship: many employees can belong to a single team.
     */
    @ManyToOne
    @JoinColumn(name = "employeeTeam")
    private Team employeeTeam;

    /**
     * Protected no-args constructor required by JPA.
     */
    protected Employee() {
        super();
    }

    /**
     * Constructs an employee with default role {@link EmployeeRole#JUNIOR} and its salary.
     *
     * @param name    first name of the employee
     * @param surname last name of the employee
     */
    public Employee(String name, String surname) {
        super(name, surname);
        setEmployeeRole(EmployeeRole.JUNIOR);
        setMonthlySalary(EmployeeRole.JUNIOR.getMonthlySalary());
    }

    /**
     * Constructs an employee with a specific role and its default salary.
     *
     * @param name         first name of the employee
     * @param surname      last name of the employee
     * @param employeeRole role of the employee
     */
    public Employee(String name, String surname, EmployeeRole employeeRole) {
        super(name, surname);
        setEmployeeRole(employeeRole);
        setMonthlySalary(employeeRole.getMonthlySalary());
    }

    /**
     * Constructs an employee with a specific role and monthly salary.
     *
     * @param name          first name of the employee
     * @param surname       last name of the employee
     * @param monthlySalary monthly salary of the employee
     * @param employeeRole  role of the employee
     */
    public Employee(String name, String surname, double monthlySalary, EmployeeRole employeeRole) {
        super(name, surname);
        setMonthlySalary(monthlySalary);
        setEmployeeRole(employeeRole);
    }

    /**
     * Returns the monthly salary of the employee.
     *
     * @return monthly salary
     */
    public double getMonthlySalary() {
        return monthlySalary;
    }

    /**
     * Sets the monthly salary of the employee.
     *
     * @param monthlySalary the new monthly salary
     */
    public void setMonthlySalary(double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    /**
     * Returns the role of the employee.
     *
     * @return employee role
     */
    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    /**
     * Sets the role of the employee.
     *
     * @param employeeRole the new role
     */
    public void setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
    }

    /**
     * Returns the list of tasks assigned to the employee.
     *
     * @return list of tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Sets the list of tasks assigned to the employee.
     *
     * @param tasks list of tasks
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the team to which this employee belongs.
     *
     * @return employee's team
     */
    public Team getEmployeeTeam() {
        return employeeTeam;
    }

    /**
     * Assigns the employee to a team.
     *
     * @param team the team to assign
     */
    public void setEmployeeTeam(Team team) {
        this.employeeTeam = team;
    }

    /**
     * Returns a list of IDs of all tasks assigned to the employee.
     *
     * @return list of task IDs
     */
    private List<Long> getAllTaskId() {
        List<Long> idTask = new ArrayList<>();
        for (Task task : tasks) {
            idTask.add(task.getTaskId());
        }
        return idTask;
    }

    /**
     * Returns a string representation of the employee, including:
     * - person information
     * - salary
     * - role
     * - assigned task IDs
     * - team ID
     *
     * @return string representation
     */
    @Override
    public String toString() {
        String teamId = employeeTeam != null ? employeeTeam.getTeamId().toString() : "null";
        return "Employee{" + super.toString() +
                "salary=" + monthlySalary +
                ", employee role=" + employeeRole +
                ", tasks=" + getAllTaskId() +
                ", team=" + teamId +
                '}';
    }
}
