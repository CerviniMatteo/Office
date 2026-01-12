package com.unimib.assignment3;

import com.unimib.assignment3.POJO.*;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import com.unimib.assignment3.repository.*;
import com.unimib.assignment3.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskTest {

    @Autowired
    private Facade facade;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    private Dipendente dipendente1;
    private Dipendente dipendente2;
    private Supervisore supervisore;

    @BeforeEach
    void setUp() {
        dipendente1 = new Dipendente("Mario", "Rossi", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);
        dipendente1 = facade.saveEmployee(dipendente1);

        dipendente2 = new Dipendente("Luigi", "Verdi", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.SENIOR);
        dipendente2 = facade.saveEmployee(dipendente2);

        supervisore = new Supervisore("Anna", "Bianchi");
        supervisore = facade.saveSupervisor(supervisore);
    }

    // Test base - creazione task
    @Test
    void testCreazioneTaskDefault() {
        Task task = facade.createTask(null);
        task = facade.saveTask(task);

        assertNotNull(task.getTaskId());
        assertEquals(TaskState.STARTED, task.getTaskState());
        assertTrue(task.getAssignedEmployees().isEmpty());
        System.out.println("Task creato: " + task);
    }

    @Test
    void testCreazioneTaskConStato() {
        Task task = new Task(TaskState.TO_BE_STARTED);
        task = facade.saveTask(task);

        assertNotNull(task.getTaskId());
        assertEquals(TaskState.TO_BE_STARTED, task.getTaskState());
        System.out.println("Task con stato INIZIATO: " + task);
    }

    @Test
    void testCreazioneTaskMultipli() {
        Task task1 = new Task();
        Task task2 = new Task();
        Task task3 = new Task(TaskState.DONE);

        task1 = facade.saveTask(task1);
        task2 = facade.saveTask(task2);
        task3 = facade.saveTask(task3);

        assertNotNull(task1.getTaskId());
        assertNotNull(task2.getTaskId());
        assertNotNull(task3.getTaskId());
        assertNotEquals(task1.getTaskId(), task2.getTaskId());
        assertNotEquals(task2.getTaskId(), task3.getTaskId());

        System.out.println("Task 1: " + task1);
        System.out.println("Task 2: " + task2);
        System.out.println("Task 3: " + task3);
    }

    @Test
    void testModificaStatoTask() {
        Task task = new Task();
        task = facade.saveTask(task);

        assertEquals(TaskState.STARTED, task.getTaskState());

        task.setTaskState(TaskState.TO_BE_STARTED);
        task = facade.saveTask(task);
        assertEquals(TaskState.TO_BE_STARTED, task.getTaskState());

        task.setTaskState(TaskState.DONE);
        task = facade.saveTask(task);
        assertEquals(TaskState.DONE, task.getTaskState());

        System.out.println("Task completato: " + task);
    }

    @Test
    void testTaskConStatoNull() {
        Task task = new Task(null);
        task = facade.saveTask(task);

        assertNotNull(task.getTaskId());
        assertEquals(TaskState.STARTED, task.getTaskState());
        System.out.println("Task con stato default: " + task);
    }

    // Test query repository
    @Test
    void testFindByTaskState() {
        Task task1 = new Task(TaskState.STARTED);
        Task task2 = new Task(TaskState.STARTED);
        Task task3 = new Task(TaskState.TO_BE_STARTED);

        task1 = facade.saveTask(task1);
        task2 = facade.saveTask(task2);
        task3 = facade.saveTask(task3);

        List<Task> tasksDaIniziare = taskRepository.findByTaskState(TaskState.STARTED);
        List<Task> tasksIniziati = taskRepository.findByTaskState(TaskState.TO_BE_STARTED);

        assertTrue(tasksDaIniziare.size() >= 2);
        assertFalse(tasksIniziati.isEmpty());
        System.out.println("Tasks DA_INIZIARE: " + tasksDaIniziare.size());
        System.out.println("Tasks INIZIATO: " + tasksIniziati.size());
    }

    @Test
    void testCountByTaskState() {
        long countInizialeDA = taskRepository.countByTaskState(TaskState.STARTED);
        long countInizialeIN = taskRepository.countByTaskState(TaskState.TO_BE_STARTED);
        long countInizialeFI = taskRepository.countByTaskState(TaskState.DONE);

        facade.saveTask(new Task(TaskState.STARTED));
        facade.saveTask(new Task(TaskState.STARTED));
        facade.saveTask(new Task(TaskState.TO_BE_STARTED));
        facade.saveTask(new Task(TaskState.DONE));

        long countDaIniziare = taskRepository.countByTaskState(TaskState.STARTED);
        long countIniziato = taskRepository.countByTaskState(TaskState.TO_BE_STARTED);
        long countFinito = taskRepository.countByTaskState(TaskState.DONE);

        assertEquals(countInizialeDA + 2, countDaIniziare);
        assertEquals(countInizialeIN + 1, countIniziato);
        assertEquals(countInizialeFI + 1, countFinito);
        System.out.println("Conteggio per stato - DA_INIZIARE: " + countDaIniziare +
                ", INIZIATO: " + countIniziato + ", FINITO: " + countFinito);
    }

    // Test incrociati Task-Dipendente
    @Test
    void testAssegnazioneDipendenteATask() {
        Task task = new Task(TaskState.TO_BE_STARTED);
        task = facade.saveTask(task);

        // Assegnazione manuale
        task.assignEmployee(dipendente1);
        dipendente1.getTasks().add(task);

        task = facade.saveTask(task);
        dipendente1 = facade.saveEmployee(dipendente1);

        assertEquals(1, task.getAssignedEmployees().size());
        assertTrue(task.hasEmployee(dipendente1));
        System.out.println("Task con dipendente assegnato: " + task);
    }

    @Test
    void testAssegnazioneDipendenteTramiteService() {
        Task task = new Task(TaskState.TO_BE_STARTED);
        task = facade.saveTask(task);

        task = taskService.assignEmployeeToTask(task.getTaskId(), dipendente1.getId());

        assertNotNull(task);
        assertEquals(1, task.getAssignedEmployees().size());
        assertTrue(task.hasEmployee(dipendente1));
        System.out.println("Task assegnato tramite service: " + task);
    }

    @Test
    void testAssegnazioneDuplicataNonPermessa() {
        Task task = new Task(TaskState.TO_BE_STARTED);
        task = facade.saveTask(task);

        taskService.assignEmployeeToTask(task.getTaskId(), dipendente1.getId());

        final Long taskId = task.getTaskId();
        final Long dipId = dipendente1.getId();

        assertThrows(IllegalStateException.class, () -> {
            taskService.assignEmployeeToTask(taskId, dipId);
        });
        System.out.println("Validazione corretta: dipendente non può essere assegnato due volte");
    }

    @Test
    void testFindTasksByDipendente() {
        Task task1 = facade.saveTask(new Task(TaskState.TO_BE_STARTED));
        Task task2 = facade.saveTask(new Task(TaskState.TO_BE_STARTED));
        Task task3 = facade.saveTask(new Task(TaskState.STARTED));

        task1.assignEmployee(dipendente1);
        dipendente1.getTasks().add(task1);

        task2.assignEmployee(dipendente1);
        dipendente1.getTasks().add(task2);

        task3.assignEmployee(dipendente2);
        dipendente2.getTasks().add(task3);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveTask(task3);
        facade.saveEmployee(dipendente1);
        facade.saveEmployee(dipendente2);

        List<Task> tasksDipendente1 = taskRepository.findTasksByDipendente(dipendente1);
        List<Task> tasksDipendente2 = taskRepository.findTasksByDipendente(dipendente2);

        assertTrue(tasksDipendente1.size() >= 2);
        assertFalse(tasksDipendente2.isEmpty());
        System.out.println("Tasks di " + dipendente1.getNome() + ": " + tasksDipendente1.size());
        System.out.println("Tasks di " + dipendente2.getNome() + ": " + tasksDipendente2.size());
    }

    @Test
    void testFindTasksWithoutDipendenti() {
        Task task1 = facade.saveTask(new Task(TaskState.STARTED));
        Task task2 = facade.saveTask(new Task(TaskState.TO_BE_STARTED));
        Task task3 = facade.saveTask(new Task(TaskState.TO_BE_STARTED));

        task2.assignEmployee(dipendente1);
        dipendente1.getTasks().add(task2);
        facade.saveTask(task2);
        facade.saveEmployee(dipendente1);

        List<Task> tasksNonAssegnati = taskRepository.findTasksWithoutDipendenti();

        assertTrue(tasksNonAssegnati.size() >= 2);
        assertTrue(tasksNonAssegnati.stream().anyMatch(t -> t.getTaskId().equals(task1.getTaskId())));
        assertTrue(tasksNonAssegnati.stream().anyMatch(t -> t.getTaskId().equals(task3.getTaskId())));
        System.out.println("Tasks senza dipendenti: " + tasksNonAssegnati.size());
    }

    @Test
    void testFindTasksWithMoreThanNDipendenti() {
        Task task1 = facade.saveTask(new Task(TaskState.TO_BE_STARTED));
        Task task2 = facade.saveTask(new Task(TaskState.TO_BE_STARTED));

        Dipendente dipendente3 = new Dipendente("Carlo", "Neri", EmployeeRole.JUNIOR);
        dipendente3 = facade.saveEmployee(dipendente3);

        task1.assignEmployee(dipendente1);
        task1.assignEmployee(dipendente2);
        task1.assignEmployee(dipendente3);

        dipendente1.getTasks().add(task1);
        dipendente2.getTasks().add(task1);
        dipendente3.getTasks().add(task1);

        task2.assignEmployee(dipendente1);
        dipendente1.getTasks().add(task2);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveEmployee(dipendente1);
        facade.saveEmployee(dipendente2);
        facade.saveEmployee(dipendente3);

        List<Task> tasksConMoltiDipendenti = taskRepository.findTasksWithMoreThanNDipendenti(1);

        assertTrue(tasksConMoltiDipendenti.size() >= 1);
        assertTrue(tasksConMoltiDipendenti.stream()
                .anyMatch(t -> t.getTaskId().equals(task1.getTaskId())));
        System.out.println("Tasks con più di 1 dipendente: " + tasksConMoltiDipendenti.size());
    }

    // Test validazioni service
    @Test
    void testNonPossoAssegnareDipendenteATaskFinito() {
        Task task = new Task(TaskState.DONE);
        task = facade.saveTask(task);

        final Long taskId = task.getTaskId();
        final Long dipId = dipendente1.getId();

        assertThrows(IllegalStateException.class, () -> {
            taskService.assignEmployeeToTask(taskId, dipId);
        });
        System.out.println("Validazione corretta: impossibile assegnare a task finito");
    }

    @Test
    void testCambioStatoTaskConValidazione() {
        Task task = new Task(TaskState.TO_BE_STARTED);
        task = facade.saveTask(task);

        task = taskService.changeTaskState(task.getTaskId(), TaskState.STARTED);
        assertEquals(TaskState.STARTED, task.getTaskState());

        task = taskService.changeTaskState(task.getTaskId(), TaskState.DONE);
        assertEquals(TaskState.DONE, task.getTaskState());

        final Long taskId = task.getTaskId();

        assertThrows(IllegalStateException.class, () -> {
            taskService.changeTaskState(taskId, TaskState.TO_BE_STARTED);
        });
        System.out.println("Validazione stati corretta");
    }

    @Test
    void testCambioStatoNonValidoDaIniziareAFinito() {
        Task task = facade.createTask(TaskState.TO_BE_STARTED);
        task = facade.saveTask(task);

        final Long taskId = task.getTaskId();

        assertThrows(IllegalStateException.class, () -> {
            taskService.changeTaskState(taskId, TaskState.DONE);
        });
        System.out.println("Validazione corretta: DA_INIZIARE non può andare direttamente a FINITO");
    }

    // Test incrociato con Team
    @Test
    void testTaskAssegnatiATeam() {
        List<Dipendente> dipendenti = new ArrayList<>();
        dipendenti.add(dipendente1);
        dipendenti.add(dipendente2);

        Team team = new Team(dipendenti, supervisore, new ArrayList<>());
        team = facade.saveTeam(team);

        Task task1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task task2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        team.addTaskTeam(task1, "Team Task");
        team.addTaskTeam(task2, "Team Task");
        team = facade.saveTeam(team);

        assertNotNull(team.getTasksTeam());
        assertEquals(2, team.getTasksTeam().size());
        System.out.println("Team con tasks: " + team.getTasksTeam().size());
    }

    @Test
    void testRimuoviDipendenteDaTask() {
        Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        task.assignEmployee(dipendente1);
        dipendente1.getTasks().add(task);
        task = facade.saveTask(task);
        dipendente1 = facade.saveEmployee(dipendente1);

        assertEquals(1, task.getAssignedEmployees().size());

        task.removeEmployee(dipendente1);
        dipendente1.getTasks().remove(task);
        task = facade.saveTask(task);
        dipendente1 = facade.saveEmployee(dipendente1);

        assertEquals(0, task.getAssignedEmployees().size());
        System.out.println("Dipendente rimosso correttamente dal task");
    }

    @Test
    void testRimuoviDipendenteTramiteService() {
        Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        task = taskService.assignEmployeeToTask(task.getTaskId(), dipendente1.getId());

        assertEquals(1, task.getAssignedEmployees().size());

        task = taskService.removeEmployeeFromTask(task.getTaskId(), dipendente1.getId());

        assertEquals(0, task.getAssignedEmployees().size());
        System.out.println("Dipendente rimosso tramite service");
    }

    @Test
    void testCountDipendentiByTaskId() {
        Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        task.assignEmployee(dipendente1);
        task.assignEmployee(dipendente2);
        dipendente1.getTasks().add(task);
        dipendente2.getTasks().add(task);

        task = facade.saveTask(task);
        facade.saveEmployee(dipendente1);
        facade.saveEmployee(dipendente2);

        Integer count = taskRepository.countDipendentiByTaskId(task.getTaskId());

        assertEquals(2, count);
        System.out.println("Numero dipendenti sul task: " + count);
    }

    @Test
    void testFindTasksByStateWithDipendenti() {
        Task task1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task task2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task task3 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        task1.assignEmployee(dipendente1);
        dipendente1.getTasks().add(task1);

        task2.assignEmployee(dipendente2);
        dipendente2.getTasks().add(task2);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveTask(task3);
        facade.saveEmployee(dipendente1);
        facade.saveEmployee(dipendente2);

        List<Task> tasksIniziatoConDipendenti =
                taskRepository.findTasksByStateWithDipendenti(TaskState.TO_BE_STARTED);

        assertTrue(tasksIniziatoConDipendenti.size() >= 2);
        System.out.println("Tasks INIZIATO con dipendenti: " + tasksIniziatoConDipendenti.size());
    }

    @Test
    void testHasDipendenteMethod() {
        Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        assertFalse(task.hasEmployee(dipendente1));

        task.assignEmployee(dipendente1);
        task = facade.saveTask(task);

        assertTrue(task.hasEmployee(dipendente1));
        assertFalse(task.hasEmployee(dipendente2));
    }

    @Test
    void testCountDipendentiMethod() {
        Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        assertEquals(0, task.countEmployees());

        task.assignEmployee(dipendente1);
        task.assignEmployee(dipendente2);
        task = facade.saveTask(task);

        assertEquals(2, task.countEmployees());
    }

    @Test
    void testFindTasksByStateAndDipendentiCount() {
        Task task1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task task2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task task3 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        task1.assignEmployee(dipendente1);
        task1.assignEmployee(dipendente2);

        task2.assignEmployee(dipendente1);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveTask(task3);

        List<Task> tasksCon2Dipendenti =
                taskRepository.findTasksByStateAndDipendentiCount(TaskState.TO_BE_STARTED, 2);
        List<Task> tasksCon1Dipendente =
                taskRepository.findTasksByStateAndDipendentiCount(TaskState.TO_BE_STARTED, 1);

        assertFalse(tasksCon2Dipendenti.isEmpty());
        assertFalse(tasksCon1Dipendente.isEmpty());
        System.out.println("Tasks INIZIATO con 2 dipendenti: " + tasksCon2Dipendenti.size());
        System.out.println("Tasks INIZIATO con 1 dipendente: " + tasksCon1Dipendente.size());
    }

    @Test
    void testPersistenzaUguaglianzaTramiteFacade() {
        Task taskOriginale = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        Long id = taskOriginale.getTaskId();

        Task taskRecuperato = new Task();
        taskRecuperato.setTaskId(id);

        assertEquals(taskOriginale, taskRecuperato);
    }

    @Test
    void testEliminazioneTaskSenzaCancellareDipendenti() {
        // 1. Setup tramite Facade
        Dipendente d = facade.saveEmployee(new Dipendente("Test", "User"));
        Task t = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        t.assignEmployee(d);
        facade.saveTask(t);


        Long taskId = t.getTaskId();
        taskRepository.deleteById(taskId);

        assertFalse(taskRepository.findById(taskId).isPresent());
        //assertNotNull(DipendenteRepository.findById(d.getId()), "Il dipendente non dovrebbe essere rimosso se cancelliamo un task.");
    }
}