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
        facade.eliminaTuttiDipendenti();
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

        assertNotNull(dip1.getId());
        assertNotNull(dip2.getId());
        assertNotEquals(dip1.getId(), dip2.getId());
    }

    @Transactional
    @Test
    void testTrovaDipendentiPerStipendio() {
        Dipendente dip1 = creaDipendente("Matteo2", "Cervini", 1800.0, Grado.MANAGER);
        Dipendente dip2 = creaDipendente("Andrea2", "Aivaliotis", 1800.0, Grado.MANAGER);

        // nuovo metodo nel Facade per cercare per stipendio senza passare dipendente
        List<Dipendente> dipendenti1800 = facade.findDipendentiByStipendio(dip1, 1800.0);

        assertEquals(2, dipendenti1800.size());
        dipendenti1800.forEach(d -> assertEquals(1800.0, d.getStipendio(), 0.01));
        assertEquals(dip1.getId(), dipendenti1800.get(0).getId());
        assertEquals(dip2.getId(), dipendenti1800.get(1).getId());

        Dipendente dip3 = creaDipendente("Matteo3", "Cervini", 3000.0, Grado.SENIOR_SW_ENGINEER);
        List<Dipendente> dipendenti3000;
        try {
            dipendenti3000 = facade.findDipendentiByStipendio(dip3, 3000.0);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

        dip3.setGrado(Grado.MANAGER);
        dipendenti3000 = facade.findDipendentiByStipendio(dip3, 3000.0);
        assertEquals(1, dipendenti3000.size());
        assertEquals(dip3.getId(), dipendenti3000.getFirst().getId());

        List<Dipendente> dipendenti3600 = facade.findDipendentiByStipendio(dip3, 3600.0);
        assertTrue(dipendenti3600.isEmpty());
    }

    @Transactional
    @Test
    void testTrovaDipendentiPerGrado() {
        Dipendente dip1 = creaDipendente("Matteo4", "Cervini", 1800.0, Grado.JUNIOR);
        Dipendente dip2 = creaDipendente("Andrea3", "Aivaliotis", 1800.0, Grado.JUNIOR);

        List<Dipendente> junior = facade.findDipendentiByGrado(Grado.JUNIOR);
        assertEquals(2, junior.size());
        junior.forEach(d -> assertEquals(Grado.JUNIOR, d.getGrado()));
        assertEquals(dip1.getId(), junior.get(0).getId());
        assertEquals(dip2.getId(), junior.get(1).getId());

        Dipendente dip3 = creaDipendente("Matteo5", "Cervini", 3000.0, Grado.SENIOR_SW_ENGINEER);
        List<Dipendente> senior = facade.findDipendentiByGrado(Grado.SENIOR_SW_ENGINEER);
        assertEquals(1, senior.size());
        assertEquals(dip3.getId(), senior.getFirst().getId());
        assertEquals(Grado.SENIOR_SW_ENGINEER, senior.getFirst().getGrado());

        List<Dipendente> manager = facade.findDipendentiByGrado(Grado.MANAGER);
        assertTrue(manager.isEmpty());
    }

    @Transactional
    @Test
    void testFindTaskByDipendenteAndState() {
        Dipendente d = facade.saveDipendente(new Dipendente("Mario", "Rossi"));

        Task t1 = facade.saveTask(new Task());
        Task t2 = facade.saveTask(new Task());
        Task t3 = facade.saveTask(new Task());
        t3.setTaskState(TaskState.INIZIATO);
        Task t4 = facade.saveTask(new Task());

        // assegna task usando facade
        facade.assegnaDipendenteATask(t1.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t2.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t3.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t4.getIdTask(), d.getId());
        t4.setTaskState(TaskState.FINITO);

        List<Task> openTasks = facade.findTasksByDipendenteAndState(d.getId(), TaskState.DAINIZIARE);
        assertEquals(2, openTasks.size());
        openTasks.forEach(t -> assertEquals(TaskState.DAINIZIARE, t.getTaskState()));

        List<Task> startedTasks = facade.findTasksByDipendenteAndState(d.getId(), TaskState.INIZIATO);
        assertEquals(1, startedTasks.size());
        assertEquals(TaskState.INIZIATO, startedTasks.getFirst().getTaskState());

        List<Task> endedTasks = facade.findTasksByDipendenteAndState(d.getId(), TaskState.FINITO);
        assertEquals(1, endedTasks.size());
        assertEquals(TaskState.FINITO, endedTasks.getFirst().getTaskState());
    }

    // ----- NUOVI TEST AGGIUNTIVI -----
    @Test
    void testSaveAllDipendenti() {
        Dipendente d1 = new Dipendente("A", "B");
        Dipendente d2 = new Dipendente("C", "D");
        List<Dipendente> saved = facade.saveAllDipendenti(List.of(d1, d2));
        assertEquals(2, saved.size());
        assertNotNull(saved.get(0).getId());
        assertNotNull(saved.get(1).getId());
    }

    @Test
    void testGetReferenceDipendente() {
        Dipendente d = creaDipendente("Ref", "Test", 1500, Grado.JUNIOR);
        Dipendente ref = facade.getReferenceDipendente(d.getId());
        assertEquals(d.getId(), ref.getId());
    }

    @Test
    void testContaTuttiDipendenti() {
        creaDipendente("A", "B", 1000, Grado.JUNIOR);
        creaDipendente("C", "D", 1200, Grado.JUNIOR);
        long count = facade.contaTuttiDipendenti();
        assertEquals(2, count);
    }

    @Test
    void testEliminaTuttiDipendenti() {
        Dipendente d1 = creaDipendente("A", "B", 1000, Grado.JUNIOR);
        Dipendente d2 = creaDipendente("C", "D", 1200, Grado.JUNIOR);
        facade.saveAllDipendenti(List.of(d1, d2));
        facade.eliminaTuttiDipendenti();
        assertEquals(0, facade.trovaTuttiDipendenti().size());
    }

}


