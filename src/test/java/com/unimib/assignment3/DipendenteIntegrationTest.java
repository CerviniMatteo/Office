package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.Grado;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DipendenteIntegrationTest {

    @Autowired
    private Facade facade;

    @BeforeEach
    void pulisciDB() {
        facade.deleteAllDipendenti();
        facade.deleteAllTasks();
    }

    private Dipendente creaDipendente(String nome, String cognome, double stipendio, Grado grado) {
        Dipendente dipendente = new Dipendente(nome, cognome, stipendio, grado);
        return facade.saveDipendente(dipendente);
    }

    @Test
    void testCreazioneDipendente() {
        Dipendente dip1 = creaDipendente("Matteo", "Cervini", 1800.0, Grado.JUNIOR);
        Dipendente dip2 = creaDipendente("Andrea", "Aivaliotis", 1800.0, Grado.JUNIOR);

        assertNotNull(dip1.getId(), "Il primo dipendente dovrebbe avere un ID");
        assertNotNull(dip2.getId(), "Il secondo dipendente dovrebbe avere un ID");
        assertNotEquals(dip1.getId(), dip2.getId(), "Gli ID dei dipendenti non dovrebbero coincidere");
    }

    @Transactional
    @Test
    void testTrovaDipendentiPerStipendio() {
        Dipendente dip1 = creaDipendente("Matteo2", "Cervini", 1800.0, Grado.JUNIOR);
        Dipendente dip2 = creaDipendente("Andrea2", "Aivaliotis", 1800.0, Grado.JUNIOR);

        List<Dipendente> dipendenti1800 = facade.findDipendentiByStipendio(1800.0);

        assertEquals(2, dipendenti1800.size(), "Dovrebbero essere trovati due dipendenti con stipendio 1800.0");
        dipendenti1800.forEach(d -> assertEquals(1800.0, d.getStipendio(), 0.01));
        assertEquals(dip1.getId(), dipendenti1800.getFirst().getId());
        assertEquals(dip2.getId(), dipendenti1800.getLast().getId());


        Dipendente dip3 = creaDipendente("Matteo3", "Cervini", 3000.0, Grado.SENIOR_SW_ENGINEER);

        List<Dipendente> dipendenti3000 = facade.findDipendentiByStipendio(3000.0);
        assertEquals(1, dipendenti3000.size(), "Dovrebbe essere trovato un dipendente con stipendio 3000.0");
        assertEquals(dip3.getId(), dipendenti3000.getFirst().getId());

        List<Dipendente> dipendenti3600 = facade.findDipendentiByStipendio(3600.0);
        assertTrue(dipendenti3600.isEmpty(), "Non dovrebbero esserci dipendenti con stipendio 3600.0");
    }

    @Transactional
    @Test
    void testTrovaDipendentiPerGrado() {
        Dipendente dip1 = creaDipendente("Matteo4", "Cervini", 1800.0, Grado.JUNIOR);
        Dipendente dip2 = creaDipendente("Andrea3", "Aivaliotis", 1800.0, Grado.JUNIOR);

        List<Dipendente> junior = facade.findDipendentiByGrado(Grado.JUNIOR);
        assertEquals(2, junior.size(), "Dovrebbero essere trovati due dipendenti JUNIOR");
        junior.forEach(d -> assertEquals(Grado.JUNIOR, d.getGrado()));
        assertEquals(dip1.getId(), junior.getFirst().getId());
        assertEquals(dip2.getId(), junior.getLast().getId());


        Dipendente dip3 = creaDipendente("Matteo5", "Cervini", 3000.0, Grado.SENIOR_SW_ENGINEER);
        List<Dipendente> senior = facade.findDipendentiByGrado(Grado.SENIOR_SW_ENGINEER);
        assertEquals(1, senior.size(), "Dovrebbe essere trovato un dipendente SENIOR_SW_ENGINEER");
        assertEquals(dip3.getId(), senior.getFirst().getId());
        assertEquals(Grado.SENIOR_SW_ENGINEER, senior.getFirst().getGrado());

        List<Dipendente> manager = facade.findDipendentiByGrado(Grado.MANAGER);
        assertTrue(manager.isEmpty(), "Non dovrebbero esserci dipendenti MANAGER");
    }

    @Transactional
    @Test
    void testFindTaskByDipendenteAndState() {
        Dipendente d = facade.saveDipendente(
                new Dipendente("Mario", "Rossi")
        );

        Task t1 = facade.saveTask(new Task(TaskState.DAINIZIARE));
        Task t2 = facade.saveTask(new Task( TaskState.INIZIATO));
        Task t3 = facade.saveTask(new Task(TaskState.FINITO));
        Task t4 = facade.saveTask(new Task(TaskState.DAINIZIARE));
        d.setTasks(List.of(t1,t2,t3, t4));
        t1.setDipendentiAssegnati(List.of(d));
        t2.setDipendentiAssegnati(List.of(d));
        t3.setDipendentiAssegnati(List.of(d));
        t4.setDipendentiAssegnati(List.of(d));

        List<Task> openTasks =
                facade.findTasksByDipendenteAndState(d.getId(), TaskState.DAINIZIARE);

        assertEquals(2, openTasks.size());
        openTasks.forEach(t -> assertEquals(TaskState.DAINIZIARE, t.getTaskState()));
        System.out.println(openTasks);

        List<Task> startedTasks =
                facade.findTasksByDipendenteAndState(d.getId(), TaskState.INIZIATO);

        assertEquals(1, startedTasks.size());
        startedTasks.forEach(t -> assertEquals(TaskState.INIZIATO, t.getTaskState()));
        System.out.println(startedTasks);

        List<Task> endedTasks =
                facade.findTasksByDipendenteAndState(d.getId(), TaskState.FINITO);
        assertEquals(1, endedTasks.size());
        endedTasks.forEach(t -> assertEquals(TaskState.FINITO, t.getTaskState()));
        System.out.println(endedTasks);
    }

}

