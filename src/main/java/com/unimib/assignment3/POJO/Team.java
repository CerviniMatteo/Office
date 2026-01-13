package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name="team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    // orphan removal not necessary as the deletion of a team does not imply
    // the deletion of the associated employees
    @OneToMany(mappedBy = "employeeTeam", cascade = CascadeType.MERGE)
    private List<Employee> employees = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "supervisor")
    private Supervisor supervisor;

    // orphan removal not necessary as the deletion of a team does not imply
    // the deletion of the associated tasks
    @OneToMany(mappedBy = "teamTask", cascade = CascadeType.MERGE)
    private List<Task> tasks = new ArrayList<>();

    protected Team() {
    }

    public Team(Supervisor supervisor) {
        supervisor.addSupervisedTeams(this);
    }
    public Team(List<Employee> employees, Supervisor supervisor) {
        setEmployees(employees);
        supervisor.addSupervisedTeams(this);
    }
    public Team(List<Employee> employees, Supervisor supervisor, List<Task> tasks) {
        setEmployees(employees);
        supervisor.addSupervisedTeams(this);
        setTasks(tasks);
    }

    public Long getTeamId() {
        return teamId;
    }
    //TODO non serve il setIdTeam in quanto l'id viene generato automaticamente

    public List<Employee> getEmployees() {
        return employees;
    }
    //TODO per motivi di progettazione la set della lista di dipendenti viene usato solo all'interno della
    // classe per gestire la relazione bidirezionale, al di fuori della classe per aggiungere una lista di
    // dipendenti si usa un ciclo con addDipendente --> nella doc.
    private void setEmployees(List<Employee> employees) {
        removeAllEmployee();
        for(Employee employee : employees) {
            employee.setEmployeeTeam(this);
        }
        this.employees = employees;
    }
    private void setEmployee(Employee employee) {
        this.employees.add(employee);
    }
    public void addEmployee(Employee employee) {
        if(!employees.contains(employee)){
            setEmployee(employee);
            employee.setEmployeeTeam(this);
        }
    }
    public void removeAllEmployee() {
        for(Employee employee : employees) {
            employee.setEmployeeTeam(null);
        }
        employees.clear();
    }
    public void removeEmployee(Employee employee) {
        if(employees.remove(employee)){
            employee.setEmployeeTeam(null);
        }
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }
    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public List<Task> getTasks() {
        return tasks;
    }
    private void setTasks(List<Task> tasks) {
        removeAllTasks();
        for(Task task : tasks) {
            task.setTeamTask(this);
        }
        this.tasks = tasks;
    }
    private void setTasks(Task task) {
        this.tasks.add(task);
    }
    public void addTask(Task task) {
        if(!tasks.contains(task)){
            setTasks(task);
            task.setTeamTask(this);
        }
    }
    public void removeAllTasks() {
        for(Task task : tasks) {
            task.setTeamTask(null);
        }
        tasks.clear();
    }
    public void removeTask(Task task) {
        if(tasks.remove(task)){
            task.setTeamTask(null);
        }
    }

    @Override
    public String toString() {
        return "Team{" +
                "team id=" + teamId +
                ", employees=" + employees +
                ", supervisor=" + supervisor +
                ", tasks=" + tasks +
                '}';
    }
}
