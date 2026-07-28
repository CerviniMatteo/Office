package com.unimib.backend;

import com.unimib.backend.POJO.Supervisor;
import com.unimib.backend.POJO.Task;
import com.unimib.backend.POJO.Team;
import com.unimib.backend.POJO.Employee;
import com.unimib.backend.enums.TaskState;
import com.unimib.backend.enums.WorkerRole;
import com.unimib.backend.facade.EmployeeFacade;
import com.unimib.backend.facade.SupervisorFacade;
import com.unimib.backend.facade.TaskFacade;
import com.unimib.backend.facade.TeamFacade;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeamIntegrationTest {
    
    @Autowired
    private SupervisorFacade supervisorFacade;
    @Autowired
    private EmployeeFacade employeeFacade;
    @Autowired
    private TeamFacade teamFacade;
    @Autowired
    private TaskFacade taskFacade;

    /**
     * Helper method to create and save a supervisor via the facade.*/
    private Supervisor createSupervisor() {
        return supervisorFacade.createSupervisor("nome" ,"cognome");
    }
    /**
     * Helper method to create and save an Employee via the facade.*/
    private Employee createEmployee() {
        return employeeFacade.createEmployee("nome", "cognome");
    }
    /**
     * Helper method to create and save an Employee with the salary and employee role via the facade.*/
    private Employee createEmployee(double monthlySalary, WorkerRole workerRole) {
        return employeeFacade.createEmployee("nome", "cognome", monthlySalary, workerRole);
    }

    /**
     * Tests creating, saving, and deleting teams, including exception handling.
     */
    @Test
    void createSaveDeleteTeamTest() {
        System.out.println("----------Create save team test----------");
        // Create supervisor
        Supervisor supervisor1 = supervisorFacade.saveSupervisor(createSupervisor());

        // Create a team with a supervisor
        Team teamOnlySupervisor = teamFacade.createTeam(supervisor1);
        teamOnlySupervisor = teamFacade.saveTeam(teamOnlySupervisor);
        assertNotNull(teamOnlySupervisor.getTeamId());
        System.out.println(teamOnlySupervisor);
        
        // Create employees
        Employee employee1 = createEmployee();
        Employee employee2 = createEmployee();

        // List of employee
        List<Employee> employees1 = new ArrayList<>(List.of(employee1, employee2));

        // Create a team with employees and supervisor
        Team teamEmployeesSupervisor = teamFacade.createTeam(employees1, supervisor1);
        teamEmployeesSupervisor = teamFacade.saveTeam(teamEmployeesSupervisor);
        assertNotNull(teamEmployeesSupervisor.getTeamId());
        System.out.println(teamEmployeesSupervisor);

        // Create tasks
        Task task1 = taskFacade.createTask(TaskState.TO_BE_STARTED);
        task1 = taskFacade.saveTask(task1);
        Task task2 = taskFacade.createTask(TaskState.STARTED);
        task2 = taskFacade.saveTask(task2);

        // List of tasks
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with employees, supervisor and tasks
        Employee employee3 = createEmployee();
        List<Employee> employees2 = new ArrayList<>(List.of(employee3));
        Team teamEmployeesSupervisorTasks = teamFacade.createTeam(employees2, supervisor1, tasks);
        teamEmployeesSupervisorTasks = teamFacade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        System.out.println("----------Delete team test----------");
        Long teamId = teamEmployeesSupervisorTasks.getTeamId();
        System.out.println("Id team: " + teamId);
        List<Employee> employees = teamFacade.getEmployeesByTeamId((teamId));
        Supervisor supervisor = teamFacade.getSupervisorByTeamId(teamId);
        List<Task> tasksTeam = teamFacade.getTasksByTeamId(teamId);
        teamFacade.deleteTeam(teamEmployeesSupervisorTasks);
        for (Employee employee : employees) {
            assertNull(employee.getEmployeeTeam());
            System.out.println("Employee: " + employee.getWorkerId() + ", employeeTeam: null");
        }
        System.out.println("Supervisor: " + supervisor.getWorkerId() + ", supervisedTeams: " + supervisor.getSupervisedTeams().stream().map(team -> team.getTeamId().toString()).collect(Collectors.joining(", ")));
        for (Task task : tasksTeam) {
            assertNull(task.getTeamTask());
            System.out.println("Task: " + task.getTaskId() + ", taskTeam: null");
        }
        assertFalse(teamFacade.getTeamById(teamId).isPresent());
        System.out.println("Team deleted successfully");

        System.out.println("----------Delete team by id test----------");
        teamId = teamEmployeesSupervisor.getTeamId();
        System.out.println("Id team: " + teamId);
        employees = teamFacade.getEmployeesByTeamId(teamId);
        supervisor = teamFacade.getSupervisorByTeamId(teamId);
        teamFacade.deleteTeamById(teamId);
        for (Employee employee : employees) {
            assertNull(employee.getEmployeeTeam());
            System.out.println("Employee: " + employee.getWorkerId() + ", employeeTeam: null");
        }
        System.out.println("Supervisor: " + supervisor.getWorkerId() + ", supervisedTeams: " + supervisor.getSupervisedTeams().stream().map(team -> team.getTeamId().toString()).collect(Collectors.joining(", ")));
        assertFalse(teamFacade.getTeamById(teamId).isPresent());
        System.out.println("Team deleted by id successfully");

        // Test error messages
        System.out.println("----------Create save delete error team test----------");
        assertThrows(IllegalArgumentException.class, () -> teamFacade.createTeam(null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.createTeam(null, supervisor1));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.saveTeam(null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.deleteTeam(null));
        System.out.println("----------Team error test end successfully----------");

        System.out.println("----------End create save delete team test----------");
    }

    /**
     * Tests all the methods related to team employees and exception messages for those methods.
     */
    @Test
    void teamEmployeesTest() {
        System.out.println("----------Team employees test----------");
        // Create supervisor
        Supervisor supervisor1 = supervisorFacade.saveSupervisor(createSupervisor());
        // Create employees
        Employee employee1 = employeeFacade.saveEmployee(createEmployee());
        Employee employee2 = employeeFacade.saveEmployee(createEmployee());
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2));

        // Create a team with employees and supervisor
        Team teamEmployees = teamFacade.createTeam(employees, supervisor1);
        teamEmployees = teamFacade.saveTeam(teamEmployees);
        assertNotNull(teamEmployees.getTeamId());
        System.out.println(teamEmployees);

        // Get employees in a team
        System.out.println("----------Team get employees test----------");
        List<Employee> employeesTeam1 = teamFacade.getEmployeesInTeam(teamEmployees);
        assertEquals(2, employeesTeam1.size());
        System.out.println(employeesTeam1);

        //Get employees by team id
        System.out.println("----------Team get employees by team id test----------");
        employeesTeam1 = teamFacade.getEmployeesByTeamId(teamEmployees.getTeamId());
        assertEquals(2, employeesTeam1.size());
        System.out.println(employeesTeam1);

        // Add an employee to a team
        System.out.println("----------Team add employee test----------");
        Employee employee3 = employeeFacade.saveEmployee(createEmployee());
        teamFacade.addEmployeeToTeam(teamEmployees, employee3);
        employeesTeam1 = teamFacade.getEmployeesInTeam(teamEmployees);
        assertEquals(3, employeesTeam1.size());
        System.out.println(teamEmployees);

        // Add an employee to another team
        System.out.println("----------Team add employee to another team test----------");
        Supervisor supervisor2 = supervisorFacade.saveSupervisor(createSupervisor());
        Team teamToAddEmployee = teamFacade.createTeam(supervisor2);
        teamToAddEmployee = teamFacade.saveTeam(teamToAddEmployee);
        teamFacade.addEmployeeToTeam(teamToAddEmployee, employee3);
        employeesTeam1 = teamFacade.getEmployeesInTeam(teamEmployees);
        assertEquals(2, employeesTeam1.size());
        System.out.println(teamEmployees);
        List<Employee> employeesTeam2 = teamFacade.getEmployeesInTeam(teamToAddEmployee);
        assertEquals(1, employeesTeam2.size());
        System.out.println(teamToAddEmployee);

        // Remove one employee from a team
        System.out.println("----------Team remove employee test----------");
        teamFacade.removeEmployeeFromTeam(teamToAddEmployee, employee3);
        employeesTeam2 = teamFacade.getEmployeesInTeam(teamToAddEmployee);
        assertEquals(0, employeesTeam2.size());
        System.out.println(teamToAddEmployee);
        System.out.println(employee3);

        // Remove all employees from a team
        System.out.println("----------Team remove all employees test----------");
        teamFacade.removeAllEmployeesFromTeam(teamEmployees);
        employeesTeam1 = teamFacade.getEmployeesInTeam(teamEmployees);
        assertEquals(0, employeesTeam1.size());
        System.out.println(teamEmployees);
        System.out.println(employee1);
        System.out.println(employee2);

        // Test error messages
        System.out.println("----------Team employees error test----------");
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getEmployeesInTeam(null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.addEmployeeToTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.removeEmployeeFromTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.removeAllEmployeesFromTeam(null));
        System.out.println("----------Team employees error test end successfully----------");

        System.out.println("----------End team employees test----------");
    }

    /**
     * Tests all the methods related to team supervisor and exception messages for those methods.
     */
    @Test
    void teamSupervisorTest(){
        System.out.println("----------Team supervisor test----------");

        // Create supervisor
        Supervisor supervisor1 = supervisorFacade.saveSupervisor(createSupervisor());

        // Create a team with a supervisor
        Team teamOnlySupervisor = teamFacade.createTeam(supervisor1);
        teamOnlySupervisor = teamFacade.saveTeam(teamOnlySupervisor);
        assertNotNull(teamOnlySupervisor.getTeamId());
        System.out.println(teamOnlySupervisor);

        // Get the supervisor in a team
        System.out.println("----------Get supervisor test----------");
        Supervisor supervisorTeam = teamFacade.getTeamSupervisor(teamOnlySupervisor);
        assertEquals(supervisor1, supervisorTeam);
        System.out.println(supervisorTeam);

        //Get supervisor by team id
        System.out.println("----------Get supervisor by team id test----------");
        supervisorTeam = teamFacade.getSupervisorByTeamId(teamOnlySupervisor.getTeamId());
        assertEquals(supervisor1, supervisorTeam);
        System.out.println(supervisorTeam);

        // Dont need test setSupervisor

        // Test error messages
        System.out.println("----------Team supervisor error test----------");
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getTeamSupervisor(null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getSupervisorByTeamId(null));
        System.out.println("----------Team supervisor error test end successfully----------");

        System.out.println("----------End team supervisor test----------");
    }

    /**
     * Tests all the methods related to team tasks and exception messages for those methods.
     */
    @Test
    void teamTasksTest(){
        System.out.println("----------Team tasks test----------");

        // Create supervisor
        Supervisor supervisor1 = supervisorFacade.saveSupervisor(createSupervisor());

        // Create employees
        Employee employee1 = employeeFacade.saveEmployee(createEmployee());
        Employee employee2 = employeeFacade.saveEmployee(createEmployee());
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2));

        // Create tasks
        Task task1 = taskFacade.createTask(TaskState.TO_BE_STARTED);
        task1 = taskFacade.saveTask(task1);
        Task task2 = taskFacade.createTask(TaskState.STARTED);
        task2 = taskFacade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with employees, supervisor and tasks
        Team teamEmployeesSupervisorTasks = teamFacade.createTeam(employees, supervisor1, tasks);
        teamEmployeesSupervisorTasks = teamFacade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        // Get tasks in a team
        System.out.println("----------Get tasks test----------");
        List<Task> tasksTeam = teamFacade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Get tasks by team id
        System.out.println("----------Get tasks by team id test----------");
        tasksTeam = teamFacade.getTasksByTeamId(teamEmployeesSupervisorTasks.getTeamId());
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Add a task to a team
        System.out.println("----------Add task test----------");
        Task task3 = taskFacade.createTask(TaskState.TO_BE_STARTED);
        task3 = taskFacade.saveTask(task3);
        teamFacade.addTaskToTeam(teamEmployeesSupervisorTasks, task3);
        tasksTeam = teamFacade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(3, tasksTeam.size());
        System.out.println(teamEmployeesSupervisorTasks);

        // Add a task to another team
        System.out.println("----------Add task to another team test----------");
        Team teamToAddTask = teamFacade.createTeam(supervisor1);
        teamToAddTask = teamFacade.saveTeam(teamToAddTask);
        teamFacade.addTaskToTeam(teamToAddTask, task3);
        tasksTeam = teamFacade.getTeamTasks(teamToAddTask);
        assertEquals(1, tasksTeam.size());
        System.out.println(teamToAddTask);
        List<Task> tasksTeam2 = teamFacade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(2, tasksTeam2.size());
        System.out.println(teamEmployeesSupervisorTasks);

        // Remove one task from a team
        System.out.println("----------Remove task test----------");
        teamFacade.removeTaskFromTeam(teamToAddTask, task3);
        tasksTeam2 = teamFacade.getTeamTasks(teamToAddTask);
        assertEquals(0, tasksTeam2.size());
        System.out.println(teamToAddTask);
        System.out.println(task3);

        // Remove all tasks from a team
        System.out.println("----------Remove all tasks test----------");
        teamFacade.removeAllTasksFromTeam(teamEmployeesSupervisorTasks);
        tasksTeam = teamFacade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(0, tasksTeam.size());
        System.out.println(teamEmployeesSupervisorTasks);
        System.out.println(task1);
        System.out.println(task2);

        // Test error messages
        System.out.println("----------Team tasks error test----------");
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getTeamTasks(null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.addTaskToTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.removeTaskFromTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.removeAllTasksFromTeam(null));
        System.out.println("----------Team tasks error test end successfully----------");

        System.out.println("----------End team tasks test----------");
    }

    /**
     * Tests all the methods related to getting teams and exception messages for those methods.
     */
    @Test
    void getTeamTest(){
        System.out.println("----------Get team test----------");
        // Create supervisor
        Supervisor supervisor1 = supervisorFacade.saveSupervisor(createSupervisor());

        // Create employees
        Employee employee1 = employeeFacade.saveEmployee(createEmployee());
        Employee employee2 = employeeFacade.saveEmployee(createEmployee());
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2));

        // Create tasks
        Task task1 = taskFacade.createTask(TaskState.TO_BE_STARTED);
        task1 = taskFacade.saveTask(task1);
        Task task2 = taskFacade.createTask(TaskState.STARTED);
        task2 = taskFacade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with employees, supervisor and tasks
        Team teamEmployeesSupervisorTasks = teamFacade.createTeam(employees, supervisor1, tasks);
        teamEmployeesSupervisorTasks = teamFacade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        // Get team by id
        System.out.println("----------Get team by id test----------");
        Long teamId = teamEmployeesSupervisorTasks.getTeamId();
        if(teamFacade.getTeamById(teamId).isPresent()) {
            Team team = teamFacade.getTeamById(teamId).get();
            assertEquals(teamEmployeesSupervisorTasks, team);
            System.out.println(team);
        }
        // Get all teams
        System.out.println("----------Get all teams test----------");
        Team teamTest1 = teamFacade.createTeam(supervisor1);
        teamTest1 = teamFacade.saveTeam(teamTest1);
        Team teamTest2 = teamFacade.createTeam(supervisor1);
        teamTest2 = teamFacade.saveTeam(teamTest2);
        List<Team> teams = teamFacade.getAllTeams();
        assertTrue(teams.contains(teamEmployeesSupervisorTasks));
        System.out.println(teams.stream().map(Team::getTeamId).collect(Collectors.toList()));

        // Get teams by supervisor id
        System.out.println("----------Get teams by supervisor id test----------");
        Long idSupervisor = supervisor1.getWorkerId();
        teams = teamFacade.getTeamsBySupervisorPersonId(idSupervisor);
        assertTrue(teams.contains(teamEmployeesSupervisorTasks));
        System.out.println(teams.stream().map(Team::getTeamId).collect(Collectors.toList()));

        // Get team by employee id
        System.out.println("----------Get team by employee id test----------");
        Team teamByEmployee = teamFacade.getTeamByEmployeePersonId(employee1.getWorkerId());
        assertEquals(teamEmployeesSupervisorTasks, teamByEmployee);
        System.out.println(teamByEmployee);

        // Get team by task id
        System.out.println("----------Get team by task id test----------");
        Team teamByTask = teamFacade.getTeamByTask_Id(task1.getTaskId());
        assertEquals(teamEmployeesSupervisorTasks, teamByTask);
        System.out.println(teamByTask);

        // Test error messages
        System.out.println("----------Get team error test----------");
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getTeamById(null));
        assertFalse(teamFacade.getTeamById(0L).isPresent());
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getTeamsBySupervisorPersonId(null));
        assertThrows(EntityNotFoundException.class, () -> teamFacade.getTeamsBySupervisorPersonId(0L));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getTeamByEmployeePersonId(null));
        assertThrows(EntityNotFoundException.class, () -> teamFacade.getTeamByEmployeePersonId(0L));
        System.out.println("----------Get team error test end successfully----------");

        System.out.println("----------End get team test----------");
    }

    /**
     * Tests complex repository queries related to teams and exception messages for those methods.
     */
    @Test
    void complexRepositoryQueryTest(){
        System.out.println("----------Complex repository query test----------");
        // Create supervisor
        Supervisor supervisor1 = supervisorFacade.saveSupervisor(createSupervisor());

        // Create employees
        Employee employee1 = employeeFacade.saveEmployee(createEmployee(WorkerRole.JUNIOR.getMonthlySalary() + 100.0, WorkerRole.JUNIOR));
        Employee employee2 = employeeFacade.saveEmployee(createEmployee(WorkerRole.JUNIOR.getMonthlySalary() + 600.0, WorkerRole.JUNIOR));
        Employee employee3 = employeeFacade.saveEmployee(createEmployee( WorkerRole.MANAGER.getMonthlySalary(), WorkerRole.MANAGER));
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2, employee3));

        // Create tasks
        Task task1 = taskFacade.createTask(TaskState.TO_BE_STARTED);
        task1 = taskFacade.saveTask(task1);
        Task task2 = taskFacade.createTask(TaskState.STARTED);
        task2 = taskFacade.saveTask(task2);
        Task task3 = taskFacade.createTask(TaskState.TO_BE_STARTED);
        task3 = taskFacade.saveTask(task3);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3));

        // Create a team with employees, supervisor and tasks
        Team teamEmployeesSupervisorTasks = teamFacade.createTeam(employees, supervisor1, tasks);
        teamEmployeesSupervisorTasks = teamFacade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        // Get tasks in a team by task state
        System.out.println("----------Get tasks by task state test----------");
        Long teamId = teamEmployeesSupervisorTasks.getTeamId();
        List<Task> tasksByState = teamFacade.getTasksInTeamIdByTaskState(teamId, TaskState.TO_BE_STARTED);
        for (Task task : tasksByState) {
            assertEquals(TaskState.TO_BE_STARTED, task.getTaskState());
        }
        System.out.println(tasksByState);

        // Get employees with the salary greater than
        System.out.println("----------Get employees with salary greater than 1900.0 test----------");
        List<Employee> employeesWithSalaryGreaterThan = teamFacade.getEmployeesInTeamIdWithSalaryGreaterThan(teamId, 2900.0);
        for(Employee employee : employeesWithSalaryGreaterThan) {
            assertTrue(Double.compare(employee.getMonthlySalary(),2900)>0);
        }
        System.out.println(employeesWithSalaryGreaterThan);

        // Get employees with the salary less than
        System.out.println("----------Get employees with salary less than 3000.0 test----------");
        List<Employee> employeesWithSalaryLessThan = teamFacade.getEmployeesInTeamIdWithSalaryLessThan(teamId, 3100.0);
        for(Employee employee : employeesWithSalaryLessThan) {
            assertTrue(Double.compare(employee.getMonthlySalary(),3100)<0);
        }
        System.out.println(employeesWithSalaryLessThan);

        // Get employees with the grado as
        System.out.println("----------Get employees with employee role as JUNIOR test----------");
        List<Employee> employeesWithEmployeeRole = teamFacade.getEmployeesInTeamIdWithEmployeeRole(teamId, WorkerRole.JUNIOR);
        for(Employee employee : employeesWithEmployeeRole) {
            assertEquals(0, WorkerRole.JUNIOR.compareTo(employee.getWorkerRole()));
        }
        System.out.println(employeesWithEmployeeRole);

        // Test error messages
        System.out.println("----------Complex repository query error test----------");
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getTasksInTeamIdByTaskState(null, TaskState.TO_BE_STARTED));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getTasksInTeamIdByTaskState(teamId, null));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getEmployeesInTeamIdWithSalaryGreaterThan(teamId, 0.0));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getEmployeesInTeamIdWithSalaryLessThan(teamId, 0.0));
        assertThrows(IllegalArgumentException.class, () -> teamFacade.getEmployeesInTeamIdWithEmployeeRole(teamId, null));
        System.out.println("----------Complex repository query error test end successfully----------");

        System.out.println("----------End complex repository query test----------");
    }
}
