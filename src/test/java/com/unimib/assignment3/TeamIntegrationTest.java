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
     * Helper method to create and save a supervisor via the facade.*/
    private Employee createDipendente() {
        return facade.createEmployee("nome", "cognome");
    }

    private Employee createEmployee(double monthlySalary, EmployeeRole employeeRole) {
        return facade.createEmployee("nome", "cognome", monthlySalary, employeeRole);
    }

    @Test
    void createSaveDeleteTeamTest() {
        System.out.println("----------Create save team test----------");
        // Create supervisore
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create a team with supervisore
        Team teamOnlySupervisore = facade.createTeam(supervisor1);
        teamOnlySupervisore = facade.saveTeam(teamOnlySupervisore);
        assertNotNull(teamOnlySupervisore.getTeamId());
        System.out.println(teamOnlySupervisore);
        
        // Create dipendenti
        Employee employee1 = createDipendente();
        Employee employee2 = createDipendente();

        // List of dipendenti
        List<Employee> dipendenti1 = new ArrayList<>(List.of(employee1, employee2));

        // Create a team with dipendenti and supervisore
        Team teamDipendentiSupervisore = facade.createTeam(dipendenti1, supervisor1);
        teamDipendentiSupervisore = facade.saveTeam(teamDipendentiSupervisore);
        assertNotNull(teamDipendentiSupervisore.getTeamId());
        System.out.println(teamDipendentiSupervisore);

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);

        // List of tasks
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with dipendenti, supervisore and tasks
        Employee employee3 = createDipendente();
        List<Employee> dipendenti2 = new ArrayList<>(List.of(employee3));
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti2, supervisor1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getTeamId());
        System.out.println(teamDipendentiSupervisoreTasks);

        System.out.println("----------Delete team test----------");
        Long idTeam = teamDipendentiSupervisoreTasks.getTeamId();
        System.out.println("Id team: " + idTeam);
        List<Employee> dipendenti = facade.getDipendentiByTeamId((idTeam));
        Supervisor supervisor = facade.getSupervisoreByTeamId(idTeam);
        List<Task> tasksTeam = facade.getTasksByTeamId(idTeam);
        facade.deleteTeam(teamDipendentiSupervisoreTasks);
        for (Employee employee : dipendenti) {
            assertNull(employee.getEmployeeTeam());
            System.out.println("Dipendente: " + employee.getPersonId() + ", dipendenteTeam: null");
        }
        System.out.println("Supervisore: " + supervisor.getPersonId() + ", teamsSupervisionato: " + supervisor.getSupervisedTeams().stream().map(team -> team.getTeamId().toString()).collect(Collectors.joining(", ")));
        for (Task task : tasksTeam) {
            assertNull(task.getTeamTask());
            System.out.println("Task: " + task.getTaskId() + ", taskTeam: null");
        }
        assertFalse(facade.getTeamById(idTeam).isPresent());
        System.out.println("Team deleted successfully");

        System.out.println("----------Delete team by id test----------");
        idTeam = teamDipendentiSupervisore.getTeamId();
        System.out.println("Id team: " + idTeam);
        dipendenti = facade.getDipendentiByTeamId(idTeam);
        supervisor = facade.getSupervisoreByTeamId(idTeam);
        facade.deleteTeamById(idTeam);
        for (Employee employee : dipendenti) {
            assertNull(employee.getEmployeeTeam());
            System.out.println("Dipendente: " + employee.getPersonId() + ", dipendenteTeam: null");
        }
        System.out.println("Supervisore: " + supervisor.getPersonId() + ", teamsSupervisionato: " + supervisor.getSupervisedTeams().stream().map(team -> team.getTeamId().toString()).collect(Collectors.joining(", ")));
        assertFalse(facade.getTeamById(idTeam).isPresent());
        System.out.println("Team deleted by id successfully");

        // Test error messages
        System.out.println("----------Create save delete error team test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.createTeam(null));
        List<Employee> dipendentiNull = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> facade.createTeam(dipendentiNull, supervisor1));
        assertThrows(IllegalArgumentException.class, () -> facade.saveTeam(null));
        assertThrows(IllegalArgumentException.class, () -> facade.deleteTeam(null));
        System.out.println("----------Team error test end successfully----------");

        System.out.println("----------End create save delete team test----------");
    }

    @Test
    void teamDipendentiTest() {
        System.out.println("----------Team dipendenti test----------");
        // Create supervisore
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());
        // Create dipendenti
        Employee employee1 = facade.saveEmployee(createDipendente());
        Employee employee2 = facade.saveEmployee(createDipendente());
        List<Employee> dipendenti = new ArrayList<>(List.of(employee1, employee2));

        // Create a team with dipendenti and supervisore
        Team teamDipendenti = facade.createTeam(dipendenti, supervisor1);
        teamDipendenti = facade.saveTeam(teamDipendenti);
        assertNotNull(teamDipendenti.getTeamId());
        System.out.println(teamDipendenti);

        // Get dipendenti in a team
        System.out.println("----------Team get dipendenti test----------");
        List<Employee> dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(2, dipendentiTeam1.size());
        System.out.println(dipendentiTeam1);

        //Get dipendenti by team id
        System.out.println("----------Team get dipendenti by team id test----------");
        dipendentiTeam1 = facade.getDipendentiByTeamId(teamDipendenti.getTeamId());
        assertEquals(2, dipendentiTeam1.size());
        System.out.println(dipendentiTeam1);

        // Add a dipendente to a team
        System.out.println("----------Team add dipendenti test----------");
        Employee employee3 = facade.saveEmployee(createDipendente());
        facade.addDipendenteToTeam(teamDipendenti, employee3);
        dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(3, dipendentiTeam1.size());
        System.out.println(teamDipendenti);

        // Add a dipendente to another team
        System.out.println("----------Team add dipendente to another team test----------");
        Supervisor supervisor2 = facade.saveSupervisor(createSupervisor());
        Team teamToAddDipendente = facade.createTeam(supervisor2);
        teamToAddDipendente = facade.saveTeam(teamToAddDipendente);
        facade.addDipendenteToTeam(teamToAddDipendente, employee3);
        dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(2, dipendentiTeam1.size());
        System.out.println(teamDipendenti);
        List<Employee> dipendentiTeam2 = facade.getDipendentiInTeam(teamToAddDipendente);
        assertEquals(1, dipendentiTeam2.size());
        System.out.println(teamToAddDipendente);

        // Remove one dipendente from a team
        System.out.println("----------Team remove dipendente test----------");
        facade.removeDipendenteFromTeam(teamToAddDipendente, employee3);
        dipendentiTeam2 = facade.getDipendentiInTeam(teamToAddDipendente);
        assertEquals(0, dipendentiTeam2.size());
        System.out.println(teamToAddDipendente);
        System.out.println(employee3);

        // Remove all dipendenti from a team
        System.out.println("----------Team remove all dipendenti test----------");
        facade.removeAllDipendentiFromTeam(teamDipendenti);
        dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(0, dipendentiTeam1.size());
        System.out.println(teamDipendenti);
        System.out.println(employee1);
        System.out.println(employee2);

        // Test error messages
        System.out.println("----------Team dipendenti error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getDipendentiInTeam(null));
        assertThrows(IllegalArgumentException.class, () -> facade.addDipendenteToTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> facade.removeDipendenteFromTeam(null, null));
        assertThrows(IllegalArgumentException.class, () -> facade.removeAllDipendentiFromTeam(null));
        System.out.println("----------Team dipendenti error test end successfully----------");

        System.out.println("----------End team dipendenti test----------");
    }

    @Test
    void teamSupervisoreTest(){
        System.out.println("----------Team supervisore test----------");

        // Create supervisore
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create a team with supervisore
        Team teamOnlySupervisore = facade.createTeam(supervisor1);
        teamOnlySupervisore = facade.saveTeam(teamOnlySupervisore);
        assertNotNull(teamOnlySupervisore.getTeamId());
        System.out.println(teamOnlySupervisore);

        // Get supervisore in a team
        System.out.println("----------Get supervisore test----------");
        Supervisor supervisorTeam = facade.getTeamSupervisore(teamOnlySupervisore);
        assertEquals(supervisor1, supervisorTeam);
        System.out.println(supervisorTeam);

        //Get supervisore by team id
        System.out.println("----------Get supervisore by team id test----------");
        supervisorTeam = facade.getSupervisoreByTeamId(teamOnlySupervisore.getTeamId());
        assertEquals(supervisor1, supervisorTeam);
        System.out.println(supervisorTeam);

        // Dont need test setSupervisor

        // Test error messages
        System.out.println("----------Team supervisore error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamSupervisore(null));
        assertThrows(IllegalArgumentException.class, () -> facade.getSupervisoreByTeamId(null));
        System.out.println("----------Team supervisore error test end successfully----------");

        System.out.println("----------End team supervisore test----------");
    }

    @Test
    void teamTasksTest(){
        System.out.println("----------Team tasks test----------");

        // Create supervisore
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create dipendenti
        Employee employee1 = facade.saveEmployee(createDipendente());
        Employee employee2 = facade.saveEmployee(createDipendente());
        List<Employee> dipendenti = new ArrayList<>(List.of(employee1, employee2));

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with dipendenti, supervisore and tasks
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti, supervisor1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getTeamId());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Get tasks in a team
        System.out.println("----------Get tasks test----------");
        List<Task> tasksTeam = facade.getTeamTasks(teamDipendentiSupervisoreTasks);
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Get tasks by team id
        System.out.println("----------Get tasks by team id test----------");
        tasksTeam = facade.getTasksByTeamId(teamDipendentiSupervisoreTasks.getTeamId());
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Add a task to a team
        System.out.println("----------Add task test----------");
        Task task3 = facade.createTask(TaskState.TO_BE_STARTED);
        task3 = facade.saveTask(task3);
        facade.addTaskToTeam(teamDipendentiSupervisoreTasks, task3);
        tasksTeam = facade.getTeamTasks(teamDipendentiSupervisoreTasks);
        assertEquals(3, tasksTeam.size());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Add a task to another team
        System.out.println("----------Add task to another team test----------");
        Team teamToAddTask = facade.createTeam(supervisor1);
        teamToAddTask = facade.saveTeam(teamToAddTask);
        facade.addTaskToTeam(teamToAddTask, task3);
        tasksTeam = facade.getTeamTasks(teamToAddTask);
        assertEquals(1, tasksTeam.size());
        System.out.println(teamToAddTask);
        List<Task> tasksTeam2 = facade.getTeamTasks(teamDipendentiSupervisoreTasks);
        assertEquals(2, tasksTeam2.size());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Remove one task from a team
        System.out.println("----------Remove task test----------");
        facade.removeTaskFromTeam(teamToAddTask, task3);
        tasksTeam2 = facade.getTeamTasks(teamToAddTask);
        assertEquals(0, tasksTeam2.size());
        System.out.println(teamToAddTask);
        System.out.println(task3);

        // Remove all tasks from a team
        System.out.println("----------Remove all tasks test----------");
        facade.removeAllTasksFromTeam(teamDipendentiSupervisoreTasks);
        tasksTeam = facade.getTeamTasks(teamDipendentiSupervisoreTasks);
        assertEquals(0, tasksTeam.size());
        System.out.println(teamDipendentiSupervisoreTasks);
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
        // Create supervisore
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create dipendenti
        Employee employee1 = facade.saveEmployee(createDipendente());
        Employee employee2 = facade.saveEmployee(createDipendente());
        List<Employee> dipendenti = new ArrayList<>(List.of(employee1, employee2));

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with dipendenti, supervisore and tasks
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti, supervisor1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getTeamId());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Get team by id
        System.out.println("----------Get team by id test----------");
        Long idTeam = teamDipendentiSupervisoreTasks.getTeamId();
        if(facade.getTeamById(idTeam).isPresent()) {
            Team team = facade.getTeamById(idTeam).get();
            assertEquals(teamDipendentiSupervisoreTasks, team);
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
        assertTrue(teams.contains(teamDipendentiSupervisoreTasks));
        System.out.println(teams.stream().map(Team::getTeamId).collect(Collectors.toList()));

        // Get teams by supervisore id
        System.out.println("----------Get teams by supervisore id test----------");
        Long idSupervisore = supervisor1.getPersonId();
        teams = facade.getTeamsBySupervisore_Id(idSupervisore);
        assertTrue(teams.contains(teamDipendentiSupervisoreTasks));
        System.out.println(teams.stream().map(Team::getTeamId).collect(Collectors.toList()));

        // Get team by dipendente id
        System.out.println("----------Get team by dipendente id test----------");
        Team teamByDipendente = facade.getTeamByDipendente_Id(employee1.getPersonId());
        assertEquals(teamDipendentiSupervisoreTasks, teamByDipendente);
        System.out.println(teamByDipendente);

        // Get team by task id
        System.out.println("----------Get team by task id test----------");
        Team teamByTask = facade.getTeamByTask_Id(task1.getTaskId());
        assertEquals(teamDipendentiSupervisoreTasks, teamByTask);
        System.out.println(teamByTask);

        // Test error messages
        System.out.println("----------Get team error test----------");
        assertFalse(facade.getTeamById(null).isPresent());
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamsBySupervisore_Id(0L));
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamByDipendente_Id(0L));
        assertThrows(IllegalArgumentException.class, () -> facade.getTeamByTask_Id(0L));
        System.out.println("----------Get team error test end successfully----------");

        System.out.println("----------End get team test----------");
    }

    @Test
    void complexRepositoryQueryTest(){
        System.out.println("----------Complex repository query test----------");
        // Create supervisore
        Supervisor supervisor1 = facade.saveSupervisor(createSupervisor());

        // Create dipendenti
        Employee employee1 = facade.saveEmployee(createEmployee(2000.0, EmployeeRole.JUNIOR));
        Employee employee2 = facade.saveEmployee(createEmployee(3000.0, EmployeeRole.JUNIOR));
        Employee employee3 = facade.saveEmployee(createEmployee( 4000.0, EmployeeRole.MANAGER));
        List<Employee> dipendenti = new ArrayList<>(List.of(employee1, employee2, employee3));

        // Create tasks
        Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.STARTED);
        task2 = facade.saveTask(task2);
        Task task3 = facade.createTask(TaskState.TO_BE_STARTED);
        task3 = facade.saveTask(task3);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3));

        // Create a team with dipendenti, supervisore and tasks
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti, supervisor1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getTeamId());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Get tasks in a team by task state
        System.out.println("----------Get tasks by task state test----------");
        Long idTeam = teamDipendentiSupervisoreTasks.getTeamId();
        List<Task> tasksByState = facade.getTasksInTeamIdByTaskState(idTeam, TaskState.TO_BE_STARTED);
        for (Task task : tasksByState) {
            assertEquals(TaskState.TO_BE_STARTED, task.getTaskState());
        }
        System.out.println(tasksByState);

        // Get dipendenti with the salary greater than
        System.out.println("----------Get dipendenti with salary greater than 1900.0 test----------");
        List<Employee> dipendentiWithSalaryGreaterThan = facade.getDipendentiInTeamIdWithSalaryGreaterThan(idTeam, 1900.0);
        for(Employee employee : dipendentiWithSalaryGreaterThan) {
            assertTrue(Double.compare(employee.getMonthlySalary(),1900)>0);
        }
        System.out.println(dipendentiWithSalaryGreaterThan);

        // Get dipendenti with the salary less than
        System.out.println("----------Get dipendenti with salary less than 3000.0 test----------");
        List<Employee> dipendentiWithSalaryLessThan = facade.getDipendentiInTeamIdWithSalaryLessThan(idTeam, 3000.0);
        for(Employee employee : dipendentiWithSalaryLessThan) {
            assertTrue(Double.compare(employee.getMonthlySalary(),3000)<0);
        }
        System.out.println(dipendentiWithSalaryLessThan);

        // Get dipendenti with the grado as
        System.out.println("----------Get dipendenti with grado as JUNIOR test----------");
        List<Employee> dipendentiWithGrado = facade.getDipendentiInTeamIdWithGrado(idTeam, EmployeeRole.JUNIOR);
        for(Employee employee : dipendentiWithGrado) {
            assertEquals(0, EmployeeRole.JUNIOR.compareTo(employee.getEmployeeRole()));
        }
        System.out.println(dipendentiWithGrado);

        // Test error messages
        System.out.println("----------Complex repository query error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getTasksInTeamIdByTaskState(null, TaskState.TO_BE_STARTED));
        assertThrows(IllegalArgumentException.class, () -> facade.getTasksInTeamIdByTaskState(idTeam, null));
        assertThrows(IllegalArgumentException.class, () -> facade.getDipendentiInTeamIdWithSalaryGreaterThan(idTeam, 0.0));
        assertThrows(IllegalArgumentException.class, () -> facade.getDipendentiInTeamIdWithSalaryLessThan(idTeam, 0.0));
        assertThrows(IllegalArgumentException.class, () -> facade.getDipendentiInTeamIdWithGrado(idTeam, null));
        System.out.println("----------Complex repository query error test end successfully----------");

        System.out.println("----------End complex repository query test----------");
    }
}
