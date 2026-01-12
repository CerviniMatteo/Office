package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Dipendente;
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

    private static long counter = 0;

    /**
     * Helper method to create and save a supervisor via the facade.*/
    private Supervisore createSupervisor() {
        counter++;
        return facade.saveSupervisore(new Supervisore("nome" + counter, "cognome"));
    }
    /**
     * Helper method to create and save a supervisor via the facade.*/
    private Dipendente createDipendente() {
        counter++;
        return facade.saveDipendente(new Dipendente("nome" + counter, "cognome"));
    }

    @Test
    void createSaveDeleteTeamTest() {
        System.out.println("----------Create save team test----------");
        // Create supervisore
        Supervisore supervisore1 = createSupervisor();

        // Create a team with supervisore
        Team teamOnlySupervisore = facade.createTeam(supervisore1);
        teamOnlySupervisore = facade.saveTeam(teamOnlySupervisore);
        assertNotNull(teamOnlySupervisore.getIdTeam());
        System.out.println(teamOnlySupervisore);
        
        // Create dipendenti
        Dipendente dipendente1 = createDipendente();
        Dipendente dipendente2 = createDipendente();

        // List of dipendenti
        List<Dipendente> dipendenti1 = new ArrayList<>(List.of(dipendente1, dipendente2));

        // Create a team with dipendenti and supervisore
        Team teamDipendentiSupervisore = facade.createTeam(dipendenti1, supervisore1);
        teamDipendentiSupervisore = facade.saveTeam(teamDipendentiSupervisore);
        assertNotNull(teamDipendentiSupervisore.getIdTeam());
        System.out.println(teamDipendentiSupervisore);

        // Create tasks
        Task task1 = facade.createTask(TaskState.DAINIZIARE);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.INIZIATO);
        task2 = facade.saveTask(task2);

        // List of tasks
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with dipendenti, supervisore and tasks
        Dipendente dipendente3 = createDipendente();
        List<Dipendente> dipendenti2 = new ArrayList<>(List.of(dipendente3));
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti2, supervisore1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getIdTeam());
        System.out.println(teamDipendentiSupervisoreTasks);

        System.out.println("----------Delete team test----------");
        Long idTeam = teamDipendentiSupervisoreTasks.getIdTeam();
        System.out.println("Id team: " + idTeam);
        List<Dipendente> dipendenti = facade.getDipendentiByTeamId((idTeam));
        Supervisore supervisore = facade.getSupervisoreByTeamId(idTeam);
        List<Task> tasksTeam = facade.getTasksByTeamId(idTeam);
        facade.deleteTeam(teamDipendentiSupervisoreTasks);
        for (Dipendente dipendente : dipendenti) {
            assertNull(dipendente.getDipendenteTeam());
            System.out.println("Dipendente: " + dipendente.getId() + ", dipendenteTeam: null");
        }
        System.out.println("Supervisore: " + supervisore.getId() + ", teamsSupervisionato: " + supervisore.getTeamsSupervisionato().stream().map(team -> team.getIdTeam().toString()).collect(Collectors.joining(", ")));
        for (Task task : tasksTeam) {
            assertNull(task.getTaskTeam());
            System.out.println("Task: " + task.getIdTask() + ", taskTeam: null");
        }
        assertFalse(facade.getTeamById(idTeam).isPresent());
        System.out.println("Team deleted successfully");

        System.out.println("----------Delete team by id test----------");
        idTeam = teamDipendentiSupervisore.getIdTeam();
        System.out.println("Id team: " + idTeam);
        dipendenti = facade.getDipendentiByTeamId(idTeam);
        supervisore = facade.getSupervisoreByTeamId(idTeam);
        facade.deleteTeamById(idTeam);
        for (Dipendente dipendente : dipendenti) {
            assertNull(dipendente.getDipendenteTeam());
            System.out.println("Dipendente: " + dipendente.getId() + ", dipendenteTeam: null");
        }
        System.out.println("Supervisore: " + supervisore.getId() + ", teamsSupervisionato: " + supervisore.getTeamsSupervisionato().stream().map(team -> team.getIdTeam().toString()).collect(Collectors.joining(", ")));
        assertFalse(facade.getTeamById(idTeam).isPresent());
        System.out.println("Team deleted by id successfully");

        // Test error messages
        System.out.println("----------Create save delete error team test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.createTeam(null));
        List<Dipendente> dipendentiNull = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> facade.createTeam(dipendentiNull, supervisore1));
        assertThrows(IllegalArgumentException.class, () -> facade.saveTeam(null));
        assertThrows(IllegalArgumentException.class, () -> facade.deleteTeam(null));
        System.out.println("----------Team error test end successfully----------");

        System.out.println("----------End create save delete team test----------");
    }

    @Test
    void teamDipendentiTest() {
        System.out.println("----------Team dipendenti test----------");
        // Create supervisore
        Supervisore supervisore1 = createSupervisor();

        // Create dipendenti
        Dipendente dipendente1 = createDipendente();
        Dipendente dipendente2 = createDipendente();
        List<Dipendente> dipendenti = new ArrayList<>(List.of(dipendente1, dipendente2));

        // Create a team with dipendenti and supervisore
        Team teamDipendenti = facade.createTeam(dipendenti, supervisore1);
        teamDipendenti = facade.saveTeam(teamDipendenti);
        assertNotNull(teamDipendenti.getIdTeam());
        System.out.println(teamDipendenti);

        // Get dipendenti in a team
        System.out.println("----------Team get dipendenti test----------");
        List<Dipendente> dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(2, dipendentiTeam1.size());
        System.out.println(dipendentiTeam1);

        //Get dipendenti by team id
        System.out.println("----------Team get dipendenti by team id test----------");
        dipendentiTeam1 = facade.getDipendentiByTeamId(teamDipendenti.getIdTeam());
        assertEquals(2, dipendentiTeam1.size());
        System.out.println(dipendentiTeam1);

        // Add a dipendente to a team
        System.out.println("----------Team add dipendenti test----------");
        Dipendente dipendente3 = createDipendente();
        facade.addDipendenteToTeam(teamDipendenti, dipendente3);
        dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(3, dipendentiTeam1.size());
        System.out.println(teamDipendenti);

        // Add a dipendente to another team
        System.out.println("----------Team add dipendente to another team test----------");
        Supervisore supervisore2 = createSupervisor();
        Team teamToAddDipendente = facade.createTeam(supervisore2);
        teamToAddDipendente = facade.saveTeam(teamToAddDipendente);
        facade.addDipendenteToTeam(teamToAddDipendente, dipendente3);
        dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(2, dipendentiTeam1.size());
        System.out.println(teamDipendenti);
        List<Dipendente> dipendentiTeam2 = facade.getDipendentiInTeam(teamToAddDipendente);
        assertEquals(1, dipendentiTeam2.size());
        System.out.println(teamToAddDipendente);

        // Remove one dipendente from a team
        System.out.println("----------Team remove dipendente test----------");
        facade.removeDipendenteFromTeam(teamToAddDipendente, dipendente3);
        dipendentiTeam2 = facade.getDipendentiInTeam(teamToAddDipendente);
        assertEquals(0, dipendentiTeam2.size());
        System.out.println(teamToAddDipendente);
        System.out.println(dipendente3);

        // Remove all dipendenti from a team
        System.out.println("----------Team remove all dipendenti test----------");
        facade.removeAllDipendentiFromTeam(teamDipendenti);
        dipendentiTeam1 = facade.getDipendentiInTeam(teamDipendenti);
        assertEquals(0, dipendentiTeam1.size());
        System.out.println(teamDipendenti);
        System.out.println(dipendente1);
        System.out.println(dipendente2);

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
        Supervisore supervisore1 = createSupervisor();

        // Create a team with supervisore
        Team teamOnlySupervisore = facade.createTeam(supervisore1);
        teamOnlySupervisore = facade.saveTeam(teamOnlySupervisore);
        assertNotNull(teamOnlySupervisore.getIdTeam());
        System.out.println(teamOnlySupervisore);

        // Get supervisore in a team
        System.out.println("----------Get supervisore test----------");
        Supervisore supervisoreTeam = facade.getTeamSupervisore(teamOnlySupervisore);
        assertEquals(supervisore1, supervisoreTeam);
        System.out.println(supervisoreTeam);

        //Get supervisore by team id
        System.out.println("----------Get supervisore by team id test----------");
        supervisoreTeam = facade.getSupervisoreByTeamId(teamOnlySupervisore.getIdTeam());
        assertEquals(supervisore1, supervisoreTeam);
        System.out.println(supervisoreTeam);

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
        Supervisore supervisore1 = createSupervisor();

        // Create dipendenti
        Dipendente dipendente1 = createDipendente();
        Dipendente dipendente2 = createDipendente();
        List<Dipendente> dipendenti = new ArrayList<>(List.of(dipendente1, dipendente2));

        // Create tasks
        Task task1 = facade.createTask(TaskState.DAINIZIARE);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.INIZIATO);
        task2 = facade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with dipendenti, supervisore and tasks
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti, supervisore1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getIdTeam());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Get tasks in a team
        System.out.println("----------Get tasks test----------");
        List<Task> tasksTeam = facade.getTeamTasks(teamDipendentiSupervisoreTasks);
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Get tasks by team id
        System.out.println("----------Get tasks by team id test----------");
        tasksTeam = facade.getTasksByTeamId(teamDipendentiSupervisoreTasks.getIdTeam());
        assertEquals(2, tasksTeam.size());
        System.out.println(tasksTeam);

        // Add a task to a team
        System.out.println("----------Add task test----------");
        Task task3 = facade.createTask(TaskState.DAINIZIARE);
        task3 = facade.saveTask(task3);
        facade.addTaskToTeam(teamDipendentiSupervisoreTasks, task3);
        tasksTeam = facade.getTeamTasks(teamDipendentiSupervisoreTasks);
        assertEquals(3, tasksTeam.size());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Add a task to another team
        System.out.println("----------Add task to another team test----------");
        Team teamToAddTask = facade.createTeam(supervisore1);
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
        Supervisore supervisore1 = createSupervisor();

        // Create dipendenti
        Dipendente dipendente1 = createDipendente();
        Dipendente dipendente2 = createDipendente();
        List<Dipendente> dipendenti = new ArrayList<>(List.of(dipendente1, dipendente2));

        // Create tasks
        Task task1 = facade.createTask(TaskState.DAINIZIARE);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.INIZIATO);
        task2 = facade.saveTask(task2);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        // Create a team with dipendenti, supervisore and tasks
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti, supervisore1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getIdTeam());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Get team by id
        System.out.println("----------Get team by id test----------");
        Long idTeam = teamDipendentiSupervisoreTasks.getIdTeam();
        if(facade.getTeamById(idTeam).isPresent()) {
            Team team = facade.getTeamById(idTeam).get();
            assertEquals(teamDipendentiSupervisoreTasks, team);
            System.out.println(team);
        }

        // Get all teams !!!(trova solo i test di questa transazione, da discutere)!!!!
        System.out.println("----------Get all teams test----------");
        Team teamTest1 = facade.createTeam(supervisore1);
        teamTest1 = facade.saveTeam(teamTest1);
        Team teamTest2 = facade.createTeam(supervisore1);
        teamTest2 = facade.saveTeam(teamTest2);
        /*
        teamTasksTest();
        teamDipendentiTest();
         */
        List<Team> teams = facade.getAllTeams();
        assertTrue(teams.contains(teamDipendentiSupervisoreTasks));
        System.out.println(teams.stream().map(Team::getIdTeam  ).collect(Collectors.toList()));

        // Get teams by supervisore id
        System.out.println("----------Get teams by supervisore id test----------");
        Long idSupervisore = supervisore1.getId();
        teams = facade.getTeamsBySupervisore_Id(idSupervisore);
        assertTrue(teams.contains(teamDipendentiSupervisoreTasks));
        System.out.println(teams.stream().map(Team::getIdTeam  ).collect(Collectors.toList()));

        // Get team by dipendente id
        System.out.println("----------Get team by dipendente id test----------");
        Team teamByDipendente = facade.getTeamByDipendente_Id(dipendente1.getId());
        assertEquals(teamDipendentiSupervisoreTasks, teamByDipendente);
        System.out.println(teamByDipendente);

        // Get team by task id
        System.out.println("----------Get team by task id test----------");
        Team teamByTask = facade.getTeamByTask_Id(task1.getIdTask());
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
        Supervisore supervisore1 = createSupervisor();

        // Create dipendenti
        Dipendente dipendente1 = facade.saveDipendente(new Dipendente("nome" + counter, "cognome", 2000.0, EmployeeRole.JUNIOR));
        counter++;
        Dipendente dipendente2 = facade.saveDipendente(new Dipendente("nome" + counter, "cognome", 3000.0, EmployeeRole.JUNIOR));
        counter++;
        Dipendente dipendente3 = facade.saveDipendente(new Dipendente("nome" + counter, "cognome", 4000.0, EmployeeRole.MANAGER));
        counter++;
        List<Dipendente> dipendenti = new ArrayList<>(List.of(dipendente1, dipendente2, dipendente3));

        // Create tasks
        Task task1 = facade.createTask(TaskState.DAINIZIARE);
        task1 = facade.saveTask(task1);
        Task task2 = facade.createTask(TaskState.INIZIATO);
        task2 = facade.saveTask(task2);
        Task task3 = facade.createTask(TaskState.DAINIZIARE);
        task3 = facade.saveTask(task3);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3));

        // Create a team with dipendenti, supervisore and tasks
        Team teamDipendentiSupervisoreTasks = facade.createTeam(dipendenti, supervisore1, tasks);
        teamDipendentiSupervisoreTasks = facade.saveTeam(teamDipendentiSupervisoreTasks);
        assertNotNull(teamDipendentiSupervisoreTasks.getIdTeam());
        System.out.println(teamDipendentiSupervisoreTasks);

        // Get tasks in a team by task state
        System.out.println("----------Get tasks by task state test----------");
        Long idTeam = teamDipendentiSupervisoreTasks.getIdTeam();
        List<Task> tasksByState = facade.getTasksInTeamIdByTaskState(idTeam, TaskState.DAINIZIARE);
        for (Task task : tasksByState) {
            assertEquals(TaskState.DAINIZIARE, task.getTaskState());
        }
        System.out.println(tasksByState);

        // Get dipendenti with the salary greater than
        System.out.println("----------Get dipendenti with salary greater than 1900.0 test----------");
        List<Dipendente> dipendentiWithSalaryGreaterThan = facade.getDipendentiInTeamIdWithSalaryGreaterThan(idTeam, 1900.0);
        for(Dipendente dipendente : dipendentiWithSalaryGreaterThan) {
            assertTrue(dipendente.getStipendio() > 1900);
        }
        System.out.println(dipendentiWithSalaryGreaterThan);

        // Get dipendenti with the salary less than
        System.out.println("----------Get dipendenti with salary less than 3000.0 test----------");
        List<Dipendente> dipendentiWithSalaryLessThan = facade.getDipendentiInTeamIdWithSalaryLessThan(idTeam, 3000.0);
        for(Dipendente dipendente : dipendentiWithSalaryLessThan) {
            assertTrue(dipendente.getStipendio() < 3000);
        }
        System.out.println(dipendentiWithSalaryLessThan);

        // Get dipendenti with the grado as
        System.out.println("----------Get dipendenti with grado as JUNIOR test----------");
        List<Dipendente> dipendentiWithGrado = facade.getDipendentiInTeamIdWithGrado(idTeam, EmployeeRole.JUNIOR);
        for(Dipendente dipendente : dipendentiWithGrado) {
            assertEquals(EmployeeRole.JUNIOR, dipendente.getGrado());
        }
        System.out.println(dipendentiWithGrado);

        // Test error messages
        System.out.println("----------Complex repository query error test----------");
        assertThrows(IllegalArgumentException.class, () -> facade.getTasksInTeamIdByTaskState(null, TaskState.DAINIZIARE));
        assertThrows(IllegalArgumentException.class, () -> facade.getTasksInTeamIdByTaskState(idTeam, null));
        assertThrows(IllegalArgumentException.class, () -> facade.getDipendentiInTeamIdWithSalaryGreaterThan(idTeam, 0.0));
        assertThrows(IllegalArgumentException.class, () -> facade.getDipendentiInTeamIdWithSalaryLessThan(idTeam, 0.0));
        assertThrows(IllegalArgumentException.class, () -> facade.getDipendentiInTeamIdWithGrado(idTeam, null));
        System.out.println("----------Complex repository query error test end successfully----------");

        System.out.println("----------End complex repository query test----------");
    }
}
