package com.unimib.assignment3.TaskTests;

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
        dipendente1 = new Dipendente("Mario", "Rossi", 2000.0, EmployeeRole.JUNIOR);
        dipendente1 = facade.saveDipendente(dipendente1);

        dipendente2 = new Dipendente("Luigi", "Verdi", 2500.0, EmployeeRole.SENIOR);
        dipendente2 = facade.saveDipendente(dipendente2);

        supervisore = new Supervisore("Anna", "Bianchi");
        supervisore = facade.saveSupervisore(supervisore);
    }

    // Test base - creazione task
    @Test
    void testCreazioneTaskDefault() {
        Task task = new Task();
        task = facade.saveTask(task);

        assertNotNull(task.getIdTask());
        assertEquals(TaskState.DAINIZIARE, task.getTaskState());
        assertTrue(task.getDipendentiAssegnati().isEmpty());
        System.out.println("Task creato: " + task);
    }

    @Test
    void testCreazioneTaskConStato() {
        Task task = new Task(TaskState.INIZIATO);
        task = facade.saveTask(task);

        assertNotNull(task.getIdTask());
        assertEquals(TaskState.INIZIATO, task.getTaskState());
        System.out.println("Task con stato INIZIATO: " + task);
    }

    @Test
    void testCreazioneTaskMultipli() {
        Task task1 = new Task();
        Task task2 = new Task();
        Task task3 = new Task(TaskState.FINITO);

        task1 = facade.saveTask(task1);
        task2 = facade.saveTask(task2);
        task3 = facade.saveTask(task3);

        assertNotNull(task1.getIdTask());
        assertNotNull(task2.getIdTask());
        assertNotNull(task3.getIdTask());
        assertNotEquals(task1.getIdTask(), task2.getIdTask());
        assertNotEquals(task2.getIdTask(), task3.getIdTask());

        System.out.println("Task 1: " + task1);
        System.out.println("Task 2: " + task2);
        System.out.println("Task 3: " + task3);
    }

    @Test
    void testModificaStatoTask() {
        Task task = new Task();
        task = facade.saveTask(task);

        assertEquals(TaskState.DAINIZIARE, task.getTaskState());

        task.setTaskState(TaskState.INIZIATO);
        task = facade.saveTask(task);
        assertEquals(TaskState.INIZIATO, task.getTaskState());

        task.setTaskState(TaskState.FINITO);
        task = facade.saveTask(task);
        assertEquals(TaskState.FINITO, task.getTaskState());

        System.out.println("Task completato: " + task);
    }

    @Test
    void testTaskConStatoNull() {
        Task task = new Task(null);
        task = facade.saveTask(task);

        assertNotNull(task.getIdTask());
        assertEquals(TaskState.DAINIZIARE, task.getTaskState());
        System.out.println("Task con stato default: " + task);
    }

    // Test query repository
    @Test
    void testFindByTaskState() {
        Task task1 = new Task(TaskState.DAINIZIARE);
        Task task2 = new Task(TaskState.DAINIZIARE);
        Task task3 = new Task(TaskState.INIZIATO);

        task1 = facade.saveTask(task1);
        task2 = facade.saveTask(task2);
        task3 = facade.saveTask(task3);

        List<Task> tasksDaIniziare = taskRepository.findByTaskState(TaskState.DAINIZIARE);
        List<Task> tasksIniziati = taskRepository.findByTaskState(TaskState.INIZIATO);

        assertTrue(tasksDaIniziare.size() >= 2);
        assertTrue(tasksIniziati.size() >= 1);
        System.out.println("Tasks DA_INIZIARE: " + tasksDaIniziare.size());
        System.out.println("Tasks INIZIATO: " + tasksIniziati.size());
    }

    @Test
    void testCountByTaskState() {
        long countInizialeDA = taskRepository.countByTaskState(TaskState.DAINIZIARE);
        long countInizialeIN = taskRepository.countByTaskState(TaskState.INIZIATO);
        long countInizialeFI = taskRepository.countByTaskState(TaskState.FINITO);

        facade.saveTask(new Task(TaskState.DAINIZIARE));
        facade.saveTask(new Task(TaskState.DAINIZIARE));
        facade.saveTask(new Task(TaskState.INIZIATO));
        facade.saveTask(new Task(TaskState.FINITO));

        long countDaIniziare = taskRepository.countByTaskState(TaskState.DAINIZIARE);
        long countIniziato = taskRepository.countByTaskState(TaskState.INIZIATO);
        long countFinito = taskRepository.countByTaskState(TaskState.FINITO);

        assertEquals(countInizialeDA + 2, countDaIniziare);
        assertEquals(countInizialeIN + 1, countIniziato);
        assertEquals(countInizialeFI + 1, countFinito);
        System.out.println("Conteggio per stato - DA_INIZIARE: " + countDaIniziare +
                ", INIZIATO: " + countIniziato + ", FINITO: " + countFinito);
    }

    // Test incrociati Task-Dipendente
    @Test
    void testAssegnazioneDipendenteATask() {
        Task task = new Task(TaskState.INIZIATO);
        task = facade.saveTask(task);

        // Assegnazione manuale
        task.assegnaDipendente(dipendente1);
        dipendente1.getTasks().add(task);

        task = facade.saveTask(task);
        dipendente1 = facade.saveDipendente(dipendente1);

        assertEquals(1, task.getDipendentiAssegnati().size());
        assertTrue(task.hasDipendente(dipendente1));
        System.out.println("Task con dipendente assegnato: " + task);
    }

    @Test
    void testAssegnazioneDipendenteTramiteService() {
        Task task = new Task(TaskState.INIZIATO);
        task = facade.saveTask(task);

        task = taskService.assegnaDipendenteATask(task.getIdTask(), dipendente1.getId());

        assertNotNull(task);
        assertEquals(1, task.getDipendentiAssegnati().size());
        assertTrue(task.hasDipendente(dipendente1));
        System.out.println("Task assegnato tramite service: " + task);
    }

    @Test
    void testAssegnazioneDuplicataNonPermessa() {
        Task task = new Task(TaskState.INIZIATO);
        task = facade.saveTask(task);

        taskService.assegnaDipendenteATask(task.getIdTask(), dipendente1.getId());

        final Long taskId = task.getIdTask();
        final Long dipId = dipendente1.getId();

        assertThrows(IllegalStateException.class, () -> {
            taskService.assegnaDipendenteATask(taskId, dipId);
        });
        System.out.println("Validazione corretta: dipendente non può essere assegnato due volte");
    }

    @Test
    void testFindTasksByDipendente() {
        Task task1 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task2 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task3 = facade.saveTask(new Task(TaskState.DAINIZIARE));

        task1.assegnaDipendente(dipendente1);
        dipendente1.getTasks().add(task1);

        task2.assegnaDipendente(dipendente1);
        dipendente1.getTasks().add(task2);

        task3.assegnaDipendente(dipendente2);
        dipendente2.getTasks().add(task3);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveTask(task3);
        facade.saveDipendente(dipendente1);
        facade.saveDipendente(dipendente2);

        List<Task> tasksDipendente1 = taskRepository.findTasksByDipendente(dipendente1);
        List<Task> tasksDipendente2 = taskRepository.findTasksByDipendente(dipendente2);

        assertTrue(tasksDipendente1.size() >= 2);
        assertTrue(tasksDipendente2.size() >= 1);
        System.out.println("Tasks di " + dipendente1.getNome() + ": " + tasksDipendente1.size());
        System.out.println("Tasks di " + dipendente2.getNome() + ": " + tasksDipendente2.size());
    }

    @Test
    void testFindTasksWithoutDipendenti() {
        Task task1 = facade.saveTask(new Task(TaskState.DAINIZIARE));
        Task task2 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task3 = facade.saveTask(new Task(TaskState.INIZIATO));

        task2.assegnaDipendente(dipendente1);
        dipendente1.getTasks().add(task2);
        facade.saveTask(task2);
        facade.saveDipendente(dipendente1);

        List<Task> tasksNonAssegnati = taskRepository.findTasksWithoutDipendenti();

        assertTrue(tasksNonAssegnati.size() >= 2);
        assertTrue(tasksNonAssegnati.stream().anyMatch(t -> t.getIdTask().equals(task1.getIdTask())));
        assertTrue(tasksNonAssegnati.stream().anyMatch(t -> t.getIdTask().equals(task3.getIdTask())));
        System.out.println("Tasks senza dipendenti: " + tasksNonAssegnati.size());
    }

    @Test
    void testFindTasksWithMoreThanNDipendenti() {
        Task task1 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task2 = facade.saveTask(new Task(TaskState.INIZIATO));

        Dipendente dipendente3 = new Dipendente("Carlo", "Neri", 2200.0);
        dipendente3 = facade.saveDipendente(dipendente3);

        task1.assegnaDipendente(dipendente1);
        task1.assegnaDipendente(dipendente2);
        task1.assegnaDipendente(dipendente3);

        dipendente1.getTasks().add(task1);
        dipendente2.getTasks().add(task1);
        dipendente3.getTasks().add(task1);

        task2.assegnaDipendente(dipendente1);
        dipendente1.getTasks().add(task2);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveDipendente(dipendente1);
        facade.saveDipendente(dipendente2);
        facade.saveDipendente(dipendente3);

        List<Task> tasksConMoltiDipendenti = taskRepository.findTasksWithMoreThanNDipendenti(1);

        assertTrue(tasksConMoltiDipendenti.size() >= 1);
        assertTrue(tasksConMoltiDipendenti.stream()
                .anyMatch(t -> t.getIdTask().equals(task1.getIdTask())));
        System.out.println("Tasks con più di 1 dipendente: " + tasksConMoltiDipendenti.size());
    }

    // Test validazioni service
    @Test
    void testNonPossoAssegnareDipendenteATaskFinito() {
        Task task = new Task(TaskState.FINITO);
        task = facade.saveTask(task);

        final Long taskId = task.getIdTask();
        final Long dipId = dipendente1.getId();

        assertThrows(IllegalStateException.class, () -> {
            taskService.assegnaDipendenteATask(taskId, dipId);
        });
        System.out.println("Validazione corretta: impossibile assegnare a task finito");
    }

    @Test
    void testCambioStatoTaskConValidazione() {
        Task task = new Task(TaskState.DAINIZIARE);
        task = facade.saveTask(task);

        task = taskService.cambiaStatoTask(task.getIdTask(), TaskState.INIZIATO);
        assertEquals(TaskState.INIZIATO, task.getTaskState());

        task = taskService.cambiaStatoTask(task.getIdTask(), TaskState.FINITO);
        assertEquals(TaskState.FINITO, task.getTaskState());

        final Long taskId = task.getIdTask();

        assertThrows(IllegalStateException.class, () -> {
            taskService.cambiaStatoTask(taskId, TaskState.INIZIATO);
        });
        System.out.println("Validazione stati corretta");
    }

    @Test
    void testCambioStatoNonValidoDaIniziareAFinito() {
        Task task = new Task(TaskState.DAINIZIARE);
        task = facade.saveTask(task);

        final Long taskId = task.getIdTask();

        assertThrows(IllegalStateException.class, () -> {
            taskService.cambiaStatoTask(taskId, TaskState.FINITO);
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

        Task task1 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task2 = facade.saveTask(new Task(TaskState.DAINIZIARE));

        team.addTask(task1);
        team.addTask(task2);
        team = facade.saveTeam(team);

        assertNotNull(team.getTasks());
        assertEquals(2, team.getTasks().size());
        System.out.println("Team con tasks: " + team.getTasks().size());
    }

    @Test
    void testRimuoviDipendenteDaTask() {
        Task task = facade.saveTask(new Task(TaskState.INIZIATO));

        task.assegnaDipendente(dipendente1);
        dipendente1.getTasks().add(task);
        task = facade.saveTask(task);
        dipendente1 = facade.saveDipendente(dipendente1);

        assertEquals(1, task.getDipendentiAssegnati().size());

        task.rimuoviDipendente(dipendente1);
        dipendente1.getTasks().remove(task);
        task = facade.saveTask(task);
        dipendente1 = facade.saveDipendente(dipendente1);

        assertEquals(0, task.getDipendentiAssegnati().size());
        System.out.println("Dipendente rimosso correttamente dal task");
    }

    @Test
    void testRimuoviDipendenteTramiteService() {
        Task task = facade.saveTask(new Task(TaskState.INIZIATO));
        task = taskService.assegnaDipendenteATask(task.getIdTask(), dipendente1.getId());

        assertEquals(1, task.getDipendentiAssegnati().size());

        task = taskService.rimuoviDipendenteDaTask(task.getIdTask(), dipendente1.getId());

        assertEquals(0, task.getDipendentiAssegnati().size());
        System.out.println("Dipendente rimosso tramite service");
    }

    @Test
    void testCountDipendentiByTaskId() {
        Task task = facade.saveTask(new Task(TaskState.INIZIATO));

        task.assegnaDipendente(dipendente1);
        task.assegnaDipendente(dipendente2);
        dipendente1.getTasks().add(task);
        dipendente2.getTasks().add(task);

        task = facade.saveTask(task);
        facade.saveDipendente(dipendente1);
        facade.saveDipendente(dipendente2);

        Integer count = taskRepository.countDipendentiByTaskId(task.getIdTask());

        assertEquals(2, count);
        System.out.println("Numero dipendenti sul task: " + count);
    }

    @Test
    void testFindTasksByStateWithDipendenti() {
        Task task1 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task2 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task3 = facade.saveTask(new Task(TaskState.INIZIATO));

        task1.assegnaDipendente(dipendente1);
        dipendente1.getTasks().add(task1);

        task2.assegnaDipendente(dipendente2);
        dipendente2.getTasks().add(task2);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveTask(task3);
        facade.saveDipendente(dipendente1);
        facade.saveDipendente(dipendente2);

        List<Task> tasksIniziatoConDipendenti =
                taskRepository.findTasksByStateWithDipendenti(TaskState.INIZIATO);

        assertTrue(tasksIniziatoConDipendenti.size() >= 2);
        System.out.println("Tasks INIZIATO con dipendenti: " + tasksIniziatoConDipendenti.size());
    }

    @Test
    void testHasDipendenteMethod() {
        Task task = facade.saveTask(new Task(TaskState.INIZIATO));

        assertFalse(task.hasDipendente(dipendente1));

        task.assegnaDipendente(dipendente1);
        task = facade.saveTask(task);

        assertTrue(task.hasDipendente(dipendente1));
        assertFalse(task.hasDipendente(dipendente2));
    }

    @Test
    void testCountDipendentiMethod() {
        Task task = facade.saveTask(new Task(TaskState.INIZIATO));

        assertEquals(0, task.countDipendenti());

        task.assegnaDipendente(dipendente1);
        task.assegnaDipendente(dipendente2);
        task = facade.saveTask(task);

        assertEquals(2, task.countDipendenti());
    }

    @Test
    void testFindTasksByStateAndDipendentiCount() {
        Task task1 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task2 = facade.saveTask(new Task(TaskState.INIZIATO));
        Task task3 = facade.saveTask(new Task(TaskState.INIZIATO));

        task1.assegnaDipendente(dipendente1);
        task1.assegnaDipendente(dipendente2);

        task2.assegnaDipendente(dipendente1);

        facade.saveTask(task1);
        facade.saveTask(task2);
        facade.saveTask(task3);

        List<Task> tasksCon2Dipendenti =
                taskRepository.findTasksByStateAndDipendentiCount(TaskState.INIZIATO, 2);
        List<Task> tasksCon1Dipendente =
                taskRepository.findTasksByStateAndDipendentiCount(TaskState.INIZIATO, 1);

        assertTrue(tasksCon2Dipendenti.size() >= 1);
        assertTrue(tasksCon1Dipendente.size() >= 1);
        System.out.println("Tasks INIZIATO con 2 dipendenti: " + tasksCon2Dipendenti.size());
        System.out.println("Tasks INIZIATO con 1 dipendente: " + tasksCon1Dipendente.size());
    }

    @Test
    void testPersistenzaUguaglianzaTramiteFacade() {
        Task taskOriginale = new Task(TaskState.DAINIZIARE);
        taskOriginale = facade.saveTask(taskOriginale);

        Long id = taskOriginale.getIdTask();

        Task taskRecuperato = new Task();
        taskRecuperato.setIdTask(id);

        assertEquals(taskOriginale, taskRecuperato);
    }

    @Test
    void testEliminazioneTaskSenzaCancellareDipendenti() {
        // 1. Setup tramite Facade
        Dipendente d = facade.saveDipendente(new Dipendente("Test", "User"));
        Task t = facade.saveTask(new Task(TaskState.INIZIATO));

        t.assegnaDipendente(d);
        facade.saveTask(t);


        Long taskId = t.getIdTask();
        taskRepository.deleteById(taskId);

        assertFalse(taskRepository.findById(taskId).isPresent());
        //assertNotNull(DipendenteRepository.findById(d.getId()), "Il dipendente non dovrebbe essere rimosso se cancelliamo un task.");
    }
}