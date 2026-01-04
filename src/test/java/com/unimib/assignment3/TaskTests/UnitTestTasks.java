package com.unimib.assignment3.TaskTests;

import com.unimib.assignment3.POJO.*;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class  UnitTestTasks{

    @Autowired
    private Facade facade;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testCreazioneTaskDefault() {
        Task task = new Task();
        task = facade.saveTask(task);

        assertNotNull(task.getIdTask());
        assertEquals(TaskState.DAINIZIARE, task.getTaskState());
        assertTrue(task.getDipendentiAssegnati().isEmpty());
        System.out.println(task);
    }

    @Test
    void testCreazioneTaskConStato() {
        Task task = new Task(TaskState.INIZIATO);
        task = facade.saveTask(task);

        assertNotNull(task.getIdTask());
        assertEquals(TaskState.INIZIATO, task.getTaskState());
        System.out.println(task);
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
    void testTaskConStatoNull() {
        Task task = new Task(null);
        task = facade.saveTask(task);

        assertNotNull(task.getIdTask());
        assertEquals(TaskState.DAINIZIARE, task.getTaskState());
        System.out.println("Task con stato default: " + task);
    }

    @Test
    void testImmutabilitaListaDipendenti() {
        Task task = new Task();
        Dipendente d1 = new Dipendente("Mario", "Rossi");
        task.assegnaDipendente(d1);

        // Proviamo a svuotare la lista ottenuta dal getter
        List<Dipendente> listaEsterna = task.getDipendentiAssegnati();
        listaEsterna.clear();

        // La lista interna deve essere ancora integra (dimensione 1)
        assertFalse(task.getDipendentiAssegnati().isEmpty());
    }

    @Test
    void testMetodiHelperCoerenza() {
        Task task = new Task();
        Dipendente d1 = new Dipendente("Luigi", "Verdi");

        task.assegnaDipendente(d1);

        assertTrue(task.hasDipendente(d1));
        assertEquals(1, task.countDipendenti());
    }

    @Test
    void testAssegnazioneNull() {
        Task task = new Task();

        assertDoesNotThrow(() -> task.assegnaDipendente(null));

        assertEquals(0, task.countDipendenti());
    }

    @Test
    void testUguaglianzaTask() {
        Task task1 = new Task();
        task1.setIdTask(100L);

        Task task2 = new Task();
        task2.setIdTask(100L);

        Task task3 = new Task();
        task3.setIdTask(101L);

        assertEquals(task1, task2);
        assertNotEquals(task1, task3);
    }

}