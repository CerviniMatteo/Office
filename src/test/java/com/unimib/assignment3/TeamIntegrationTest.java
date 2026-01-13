package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.facade.Facade;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.enums.EmployeeRole;
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
    private Facade facade;

    /**
     * Helper method to create and save a supervisor via the facade.*/
    private Supervisor createSupervisor() {
        return facade.createSupervisor("nome" ,"cognome");
    }
    /**
     * Helper method to create and save an Employee via the facade.*/
    private Employee createEmployee() {
        return facade.createEmployee("nome", "cognome");
    }
    /**
     * Helper method to create and save an Employee with the salary and employee role via the facade.*/
    private Employee createEmployee(double monthlySalary, EmployeeRole employeeRole) {
        return facade.createEmployee("nome", "cognome", monthlySalary, employeeRole);
    }

    @Test
    void createSaveDeleteTeamTest() {
        System.out.println("----------Create save team test----------");
        // Create supervisor
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create a team with a supervisor
        Team teamOnlySupervisor = facade.createTeam(supervisor1);
        teamOnlySupervisor = facade.saveTeam(teamOnlySupervisor);
        assertNotNull(teamOnlySupervisor.getTeamId());
        System.out.println(teamOnlySupervisor);
        
        // Create employees
        Employee employee1 = createEmployee();
        Employee employee2 = createEmployee();

        // List of employee
        List<Employee> employees1 = new ArrayList<>(List.of(employee1, employee2));

        // Create a team with employees and supervisor
        Team teamEmployeesSupervisor = facade.createTeam(employees1, supervisor1);
        teamEmployeesSupervisor = facade.saveTeam(teamEmployeesSupervisor);
        assertNotNull(teamEmployeesSupervisor.getTeamId());
        System.out.println(teamEmployeesSupervisor);

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);

        // List of tasks
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with employees, supervisor and tasks
        Employee employee3 = createEmployee();
        List<Employee> employees2 = new ArrayList<>(List.of(employee3));
        Team teamEmployeesSupervisorTasks = facade.createTeam(employees2, supervisor1, tasks);
        teamEmployeesSupervisorTasks = facade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        System.out.println("----------Delete team test----------");
        Long teamId = teamEmployeesSupervisorTasks.getTeamId();
        System.out.println("Id team: " + teamId);
        List<Employee> employees = facade.getEmployeesByTeamId((teamId));
        Supervisor supervisor = facade.getSupervisorByTeamId(teamId);
        List<Task> tasksTeam = facade.getTasksByTeamId(teamId);
        facade.deleteTeam(teamEmployeesSupervisorTasks);
        for (Employee employee : employees) {
            assertNull(employee.getEmployeeTeam());
            System.out.println("Employee: " + employee.getPersonId() + ", employeeTeam: null");
        }
        System.out.println("Supervisor: " + supervisor.getPersonId() + ", supervisedTeams: " + supervisor.getSupervisedTeams().stream().map(team -> team.getTeamId().toString()).collect(Collectors.joining(", ")));
        for (Task task : tasksTeam) {
            assertNull(task.getTeamTask());
            System.out.println("Task: " + task.getTaskId() + ", taskTeam: null");
        }
        assertFalse(facade.getTeamById(teamId).isPresent());
        System.out.println("Team deleted successfully");

        System.out.println("----------Delete team by id test----------");
        teamId = teamEmployeesSupervisor.getTeamId();
        System.out.println("Id team: " + teamId);
        employees = facade.getEmployeesByTeamId(teamId);
        supervisor = facade.getSupervisorByTeamId(teamId);
        facade.deleteTeamById(teamId);
        for (Employee employee : employees) {
            assertNull(employee.getEmployeeTeam());
            System.out.println("Employee: " + employee.getPersonId() + ", employeeTeam: null");
        }
        System.out.println("Supervisor: " + supervisor.getPersonId() + ", supervisedTeams: " + supervisor.getSupervisedTeams().stream().map(team -> team.getTeamId().toString()).collect(Collectors.joining(", ")));
        assertFalse(facade.getTeamById(teamId).isPresent());
        System.out.println("Team deleted by id successfully");

        // Test error messages
        System.out.println("----------Create save delete error team test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.createTeam(null));
        List<Employee> employeesNull = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> facade.createTeam(employeesNull, supervisor1));
        assertThrows(IllegalArgumentException.class, () -> facade.saveTeam(null));
        assertThrows(IllegalArgumentException.class, () -> facade.deleteTeam(null));
        System.out.println("----------Team error test end successfully----------");

        System.out.println("----------End create save delete team test----------");
    }

    @Test
    void teamEmployeesTest() {
        System.out.println("----------Team employees test----------");
        // Create supervisor
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());
        // Create employees
        Employee employee1 = facade.saveEmployee(createEmployee());
        Employee employee2 = facade.saveEmployee(createEmployee());
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2));

        // Create a team with employees and supervisor
        Team teamEmployees = facade.createTeam(employees, supervisor1);
        teamEmployees = facade.saveTeam(teamEmployees);
        assertNotNull(teamEmployees.getTeamId());
        System.out.println(teamEmployees);

        // Get employees in a team
        System.out.println("----------Team get employees test----------");
        List<Employee> employeesTeam1 = facade.getEmployeesInTeam(teamEmployees);
        assertEquals(2, employeesTeam1.size());
        System.out.println(employeesTeam1);

        //Get employees by team id
        System.out.println("----------Team get employees by team id test----------");
        employeesTeam1 = facade.getEmployeesByTeamId(teamEmployees.getTeamId());
        assertEquals(2, employeesTeam1.size());
        System.out.println(employeesTeam1);

        // Add an employee to a team
        System.out.println("----------Team add employee test----------");
        Employee employee3 = facade.saveEmployee(createEmployee());
        facade.addEmployeeToTeam(teamEmployees, employee3);
        employeesTeam1 = facade.getEmployeesInTeam(teamEmployees);
        assertEquals(3, employeesTeam1.size());
        System.out.println(teamEmployees);

        // Add an employee to another team
        System.out.println("----------Team add employee to another team test----------");
        Supervisor supervisor2 = facade.saveSupervisor(createSupervisor());
        Team teamToAddEmployee = facade.createTeam(supervisor2);
        teamToAddEmployee = facade.saveTeam(teamToAddEmployee);
        facade.addEmployeeToTeam(teamToAddEmployee, employee3);
        employeesTeam1 = facade.getEmployeesInTeam(teamEmployees);
        assertEquals(2, employeesTeam1.size());
        System.out.println(teamEmployees);
        List<Employee> employeesTeam2 = facade.getEmployeesInTeam(teamToAddEmployee);
        assertEquals(1, employeesTeam2.size());
        System.out.println(teamToAddEmployee);

        // Remove one employee from a team
        System.out.println("----------Team remove employee test----------");
        facade.removeEmployeeFromTeam(teamToAddEmployee, employee3);
        employeesTeam2 = facade.getEmployeesInTeam(teamToAddEmployee);
        assertEquals(0, employeesTeam2.size());
        System.out.println(teamToAddEmployee);
        System.out.println(employee3);

        // Remove all employees from a team
        System.out.println("----------Team remove all employees test----------");
        facade.removeAllEmployeesFromTeam(teamEmployees);
        employeesTeam1 = facade.getEmployeesInTeam(teamEmployees);
        assertEquals(0, employeesTeam1.size());
        System.out.println(teamEmployees);
        System.out.println(employee1);
        System.out.println(employee2);

        // Test error messages
        System.out.println("----------Team employees error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getEmployeesInTeam(null));
        assertThrows(IllegalArgumentException.class, () -> facade.addEmployeeToTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> facade.removeEmployeeFromTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> facade.removeAllEmployeesFromTeam(null));
        System.out.println("----------Team employees error test end successfully----------");

        System.out.println("----------End team employees test----------");
    }

    @Test
    void teamSupervisorTest(){
        System.out.println("----------Team supervisor test----------");

        // Create supervisor
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create a team with a supervisor
        Team teamOnlySupervisor = facade.createTeam(supervisor1);
        teamOnlySupervisor = facade.saveTeam(teamOnlySupervisor);
        assertNotNull(teamOnlySupervisor.getTeamId());
        System.out.println(teamOnlySupervisor);

        // Get the supervisor in a team
        System.out.println("----------Get supervisor test----------");
        Supervisor supervisorTeam = facade.getTeamSupervisor(teamOnlySupervisor);
        assertEquals(supervisor1, supervisorTeam);
        System.out.println(supervisorTeam);

        //Get supervisor by team id
        System.out.println("----------Get supervisor by team id test----------");
        supervisorTeam = facade.getSupervisorByTeamId(teamOnlySupervisor.getTeamId());
        assertEquals(supervisor1, supervisorTeam);
        System.out.println(supervisorTeam);

        // Dont need test setSupervisor

        // Test error messages
        System.out.println("----------Team supervisor error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamSupervisor(null));
        assertThrows(IllegalArgumentException.class, () -> facade.getSupervisorByTeamId(null));
        System.out.println("----------Team supervisor error test end successfully----------");

        System.out.println("----------End team supervisor test----------");
    }

    @Test
    void teamTasksTest(){
        System.out.println("----------Team tasks test----------");

        // Create supervisor
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create employees
        Employee employee1 = facade.saveEmployee(createEmployee());
        Employee employee2 = facade.saveEmployee(createEmployee());
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2));

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with employees, supervisor and tasks
        Team teamEmployeesSupervisorTasks = facade.createTeam(employees, supervisor1, tasks);
        teamEmployeesSupervisorTasks = facade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        // Get tasks in a team
        System.out.println("----------Get tasks test----------");
        List<Task> tasksTeam = facade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Get tasks by team id
        System.out.println("----------Get tasks by team id test----------");
        tasksTeam = facade.getTasksByTeamId(teamEmployeesSupervisorTasks.getTeamId());
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Add a task to a team
        System.out.println("----------Add task test----------");
        Task task3 = facade.createTask(TaskState.TO_BE_STARTED);
        task3 = facade.saveTask(task3);
        facade.addTaskToTeam(teamEmployeesSupervisorTasks, task3);
        tasksTeam = facade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(3, tasksTeam.size());
        System.out.println(teamEmployeesSupervisorTasks);

        // Add a task to another team
        System.out.println("----------Add task to another team test----------");
        Team teamToAddTask = facade.createTeam(supervisor1);
        teamToAddTask = facade.saveTeam(teamToAddTask);
        facade.addTaskToTeam(teamToAddTask, task3);
        tasksTeam = facade.getTeamTasks(teamToAddTask);
        assertEquals(1, tasksTeam.size());
        System.out.println(teamToAddTask);
        List<Task> tasksTeam2 = facade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(2, tasksTeam2.size());
        System.out.println(teamEmployeesSupervisorTasks);

        // Remove one task from a team
        System.out.println("----------Remove task test----------");
        facade.removeTaskFromTeam(teamToAddTask, task3);
        tasksTeam2 = facade.getTeamTasks(teamToAddTask);
        assertEquals(0, tasksTeam2.size());
        System.out.println(teamToAddTask);
        System.out.println(task3);

        // Remove all tasks from a team
        System.out.println("----------Remove all tasks test----------");
        facade.removeAllTasksFromTeam(teamEmployeesSupervisorTasks);
        tasksTeam = facade.getTeamTasks(teamEmployeesSupervisorTasks);
        assertEquals(0, tasksTeam.size());
        System.out.println(teamEmployeesSupervisorTasks);
        System.out.println(task1);
        System.out.println(task2);

        // Test error messages
        System.out.println("----------Team tasks error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamTasks(null));
        assertThrows(IllegalArgumentException.class, () -> facade.addTaskToTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> facade.removeTaskFromTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> facade.removeAllTasksFromTeam(null));
        System.out.println("----------Team tasks error test end successfully----------");

        System.out.println("----------End team tasks test----------");
    }

    @Test
    void getTeamTest(){
        System.out.println("----------Get team test----------");
        // Create supervisor
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create employees
        Employee employee1 = facade.saveEmployee(createEmployee());
        Employee employee2 = facade.saveEmployee(createEmployee());
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2));

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with employees, supervisor and tasks
        Team teamEmployeesSupervisorTasks = facade.createTeam(employees, supervisor1, tasks);
        teamEmployeesSupervisorTasks = facade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        // Get team by id
        System.out.println("----------Get team by id test----------");
        Long teamId = teamEmployeesSupervisorTasks.getTeamId();
        if(facade.getTeamById(teamId).isPresent()) {
            Team team = facade.getTeamById(teamId).get();
            assertEquals(teamEmployeesSupervisorTasks, team);
            System.out.println(team);
        }
        // Get all teams !!!(trova solo i test di questa transazione, da discutere)!!!!
        System.out.println("----------Get all teams test----------");
        Team teamTest1 = facade.createTeam(supervisor1);
        teamTest1 = facade.saveTeam(teamTest1);
        Team teamTest2 = facade.createTeam(supervisor1);
        teamTest2 = facade.saveTeam(teamTest2);
        /*
        teamTasksTest();
        teamDipendentiTest();
         */
        List<Team> teams = facade.getAllTeams();
        assertTrue(teams.contains(teamEmployeesSupervisorTasks));
        System.out.println(teams.stream().map(Team::getTeamId).collect(Collectors.toList()));

        // Get teams by supervisor id
        System.out.println("----------Get teams by supervisor id test----------");
        Long idSupervisor = supervisor1.getPersonId();
        teams = facade.getTeamsBySupervisorPersonId(idSupervisor);
        assertTrue(teams.contains(teamEmployeesSupervisorTasks));
        System.out.println(teams.stream().map(Team::getTeamId).collect(Collectors.toList()));

        // Get team by employee id
        System.out.println("----------Get team by employee id test----------");
        Team teamByEmployee = facade.getTeamByEmployeePersonId(employee1.getPersonId());
        assertEquals(teamEmployeesSupervisorTasks, teamByEmployee);
        System.out.println(teamByEmployee);

        // Get team by task id
        System.out.println("----------Get team by task id test----------");
        Team teamByTask = facade.getTeamByTask_Id(task1.getTaskId());
        assertEquals(teamEmployeesSupervisorTasks, teamByTask);
        System.out.println(teamByTask);

        // Test error messages
        System.out.println("----------Get team error test----------");
        assertFalse(facade.getTeamById(null).isPresent());
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamsBySupervisorPersonId(0L));
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamByEmployeePersonId(0L));
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamByTask_Id(0L));
        System.out.println("----------Get team error test end successfully----------");

        System.out.println("----------End get team test----------");
    }

    @Test
    void complexRepositoryQueryTest(){
        System.out.println("----------Complex repository query test----------");
        // Create supervisor
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create employees
        Employee employee1 = facade.saveEmployee(createEmployee(EmployeeRole.JUNIOR.getMonthlySalary() + 100.0, EmployeeRole.JUNIOR));
        Employee employee2 = facade.saveEmployee(createEmployee(EmployeeRole.JUNIOR.getMonthlySalary() + 600.0, EmployeeRole.JUNIOR));
        Employee employee3 = facade.saveEmployee(createEmployee( EmployeeRole.MANAGER.getMonthlySalary(), EmployeeRole.MANAGER));
        List<Employee> employees = new ArrayList<>(List.of(employee1, employee2, employee3));

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);
        Task task3 = facade.createTask(TaskState.TO_BE_STARTED);
        task3 = facade.saveTask(task3);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3));

        // Create a team with employees, supervisor and tasks
        Team teamEmployeesSupervisorTasks = facade.createTeam(employees, supervisor1, tasks);
        teamEmployeesSupervisorTasks = facade.saveTeam(teamEmployeesSupervisorTasks);
        assertNotNull(teamEmployeesSupervisorTasks.getTeamId());
        System.out.println(teamEmployeesSupervisorTasks);

        // Get tasks in a team by task state
        System.out.println("----------Get tasks by task state test----------");
        Long teamId = teamEmployeesSupervisorTasks.getTeamId();
        List<Task> tasksByState = facade.getTasksInTeamIdByTaskState(teamId, TaskState.TO_BE_STARTED);
        for (Task task : tasksByState) {
            assertEquals(TaskState.TO_BE_STARTED, task.getTaskState());
        }
        System.out.println(tasksByState);

        // Get employees with the salary greater than
        System.out.println("----------Get employees with salary greater than 1900.0 test----------");
        List<Employee> employeesWithSalaryGreaterThan = facade.getEmployeesInTeamIdWithSalaryGreaterThan(teamId, 2900.0);
        for(Employee employee : employeesWithSalaryGreaterThan) {
            assertTrue(Double.compare(employee.getMonthlySalary(),2900)>0);
        }
        System.out.println(employeesWithSalaryGreaterThan);

        // Get employees with the salary less than
        System.out.println("----------Get employees with salary less than 3000.0 test----------");
        List<Employee> employeesWithSalaryLessThan = facade.getEmployeesInTeamIdWithSalaryLessThan(teamId, 3100.0);
        for(Employee employee : employeesWithSalaryLessThan) {
            assertTrue(Double.compare(employee.getMonthlySalary(),3100)<0);
        }
        System.out.println(employeesWithSalaryLessThan);

        // Get employees with the grado as
        System.out.println("----------Get employees with employee role as JUNIOR test----------");
        List<Employee> employeesWithEmployeeRole = facade.getEmployeesInTeamIdWithEmployeeRole(teamId, EmployeeRole.JUNIOR);
        for(Employee employee : employeesWithEmployeeRole) {
            assertEquals(0, EmployeeRole.JUNIOR.compareTo(employee.getEmployeeRole()));
        }
        System.out.println(employeesWithEmployeeRole);

        // Test error messages
        System.out.println("----------Complex repository query error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getTasksInTeamIdByTaskState(null, TaskState.TO_BE_STARTED));
        assertThrows(IllegalArgumentException.class, () -> facade.getTasksInTeamIdByTaskState(teamId, null));
        assertThrows(IllegalArgumentException.class, () -> facade.getEmployeesInTeamIdWithSalaryGreaterThan(teamId, 0.0));
        assertThrows(IllegalArgumentException.class, () -> facade.getEmployeesInTeamIdWithSalaryLessThan(teamId, 0.0));
        assertThrows(IllegalArgumentException.class, () -> facade.getEmployeesInTeamIdWithEmployeeRole(teamId, null));
        System.out.println("----------Complex repository query error test end successfully----------");

        System.out.println("----------End complex repository query test----------");
    }
}
