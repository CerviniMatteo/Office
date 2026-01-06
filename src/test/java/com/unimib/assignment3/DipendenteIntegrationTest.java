package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
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
    void cleanDatabase() {
        facade.deleteAllTasks();
        facade.eliminaTuttiDipendenti();
    }

    private Dipendente creaDipendente(String nome, String cognome, EmployeeRole ruolo) {
        return facade.saveDipendente(
                new Dipendente(nome, cognome, ruolo.getMonthlySalary(), ruolo)
        );
    }


    @Test
    void shouldCreateDipendentiWithDifferentIds() {
        Dipendente d1 = creaDipendente("Matteo", "Cervini", EmployeeRole.JUNIOR);
        Dipendente d2 = creaDipendente("Andrea", "Aivaliotis", EmployeeRole.JUNIOR);

        assertNotNull(d1.getId());
        assertNotNull(d2.getId());
        assertNotEquals(d1.getId(), d2.getId());
    }


    @Test
    @Transactional
    void shouldFindDipendentiByStipendio() {
        Dipendente d1 = creaDipendente("A", "B", EmployeeRole.MANAGER);
        Dipendente d2 = creaDipendente("C", "D", EmployeeRole.MANAGER);

        d1 = facade.saveDipendente(d1);
        d2 = facade.saveDipendente(d2);

        List<Dipendente> result =
                facade.findDipendentiByStipendio(d1.getId(), d2.getMonthlySalary());

        assertEquals(2, result.size());
        result.forEach(d ->
                assertEquals(EmployeeRole.MANAGER.getMonthlySalary(), d.getMonthlySalary())
        );
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenRoleMismatchInStipendioSearch() {
        Dipendente manager = creaDipendente("Manager", "X", EmployeeRole.SENIOR_SW_ENGINEER);
        manager = facade.saveDipendente(manager);

        Dipendente finalManager = manager;
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> facade.findDipendentiByStipendio(
                        finalManager.getId(),
                        EmployeeRole.JUNIOR.getMonthlySalary()
                )
        );

        assertNotNull(ex.getMessage());
    }

    @Test
    @Transactional
    void shouldFindDipendentiByGrado() {
        Dipendente d1 = creaDipendente("A", "B", EmployeeRole.JUNIOR);
        Dipendente d2 = creaDipendente("C", "D", EmployeeRole.JUNIOR);

        List<Dipendente> junior =
                facade.findDipendentiByGrado(EmployeeRole.JUNIOR);

        assertEquals(2, junior.size());
        assertTrue(junior.stream().allMatch(d ->
                d.getEmployeeRole() == EmployeeRole.JUNIOR
        ));
    }


    @Test
    @Transactional
    void shouldFindTasksByDipendenteAndState() {
        Dipendente d = facade.saveDipendente(new Dipendente("Mario", "Rossi"));

        Task t1 = facade.saveTask(new Task());
        Task t2 = facade.saveTask(new Task());
        Task t3 = facade.saveTask(new Task());
        Task t4 = facade.saveTask(new Task());

        facade.saveTask(t3);
        facade.saveTask(t4);

        facade.assegnaDipendenteATask(t1.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t2.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t3.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t4.getIdTask(), d.getId());

        t3.setTaskState(TaskState.INIZIATO);
        t4.setTaskState(TaskState.FINITO);

        assertEquals(2,
                facade.findTasksByDipendenteAndState(d.getId(), TaskState.DAINIZIARE).size());

        assertEquals(1,
                facade.findTasksByDipendenteAndState(d.getId(), TaskState.INIZIATO).size());

        assertEquals(1,
                facade.findTasksByDipendenteAndState(d.getId(), TaskState.FINITO).size());
    }

    @Test
    void shouldSaveAllDipendenti() {
        List<Dipendente> saved = facade.saveAllDipendenti(List.of(
                new Dipendente("A", "B"),
                new Dipendente("C", "D")
        ));

        assertEquals(2, saved.size());
        assertTrue(saved.stream().allMatch(d -> d.getId() != null));
    }

    @Test
    void shouldReturnReferenceDipendente() {
        Dipendente d = creaDipendente("Ref", "Test", EmployeeRole.JUNIOR);
        Dipendente ref = facade.getReferenceDipendente(d.getId());

        assertEquals(d.getId(), ref.getId());
    }

    @Test
    void shouldCountDipendenti() {
        creaDipendente("A", "B", EmployeeRole.JUNIOR);
        creaDipendente("C", "D", EmployeeRole.JUNIOR);

        assertEquals(2, facade.contaTuttiDipendenti());
    }

    @Test
    void shouldDeleteAllDipendenti() {
        creaDipendente("A", "B", EmployeeRole.JUNIOR);
        creaDipendente("C", "D", EmployeeRole.JUNIOR);

        facade.eliminaTuttiDipendenti();

        assertTrue(facade.trovaTuttiDipendenti().isEmpty());
    }
}


