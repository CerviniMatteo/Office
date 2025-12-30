package com.unimib.assignment3.TaskTests;

import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TaskIntegrationTest {

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
}