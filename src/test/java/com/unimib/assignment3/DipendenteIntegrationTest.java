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

    // ========================= CREAZIONE =========================

    @Test
    void shouldCreateDipendentiWithDifferentIds() {
        Dipendente d1 = creaDipendente("Matteo", "Cervini", EmployeeRole.JUNIOR);
        Dipendente d2 = creaDipendente("Andrea", "Aivaliotis", EmployeeRole.JUNIOR);

        assertNotNull(d1.getId());
        assertNotNull(d2.getId());
        assertNotEquals(d1.getId(), d2.getId());
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

    // ========================= RICERCA =========================

    @Test
    @Transactional
    void shouldFindDipendentiByStipendio() {
        Dipendente manager = creaDipendente("Manager", "X", EmployeeRole.MANAGER);
        Dipendente emp1 = creaDipendente("Dip1", "Y", EmployeeRole.JUNIOR);
        Dipendente emp2 = creaDipendente("Dip2", "Z", EmployeeRole.JUNIOR);

        List<Dipendente> result = facade.findDipendentiByMonthlySalary(manager.getId(), emp1.getMonthlySalary());

        assertEquals(2, result.size());
        result.forEach(d -> assertEquals(EmployeeRole.JUNIOR.getMonthlySalary(), d.getMonthlySalary()));
    }

    @Test
    void shouldThrowExceptionWhenRoleMismatchInStipendioSearch() {
        Dipendente manager = creaDipendente("Manager", "X", EmployeeRole.SENIOR_SW_ENGINEER);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> facade.findDipendentiByMonthlySalary(
                        manager.getId(),
                        EmployeeRole.JUNIOR.getMonthlySalary()
                )
        );

        assertNotNull(ex.getMessage());
    }

    @Test
    @Transactional
    void shouldFindDipendentiByGrado() {
        Dipendente d1 = creaDipendente("E", "F", EmployeeRole.JUNIOR);
        Dipendente d2 = creaDipendente("G", "I", EmployeeRole.JUNIOR);
        Dipendente d3 = creaDipendente("H", "L", EmployeeRole.MANAGER);

        List<Dipendente> junior = facade.findDipendentiByGrado(d3.getId(), EmployeeRole.JUNIOR);

        assertEquals(2, junior.size());
        assertTrue(junior.stream().allMatch(d -> d.getEmployeeRole() == EmployeeRole.JUNIOR));
    }

    @Test
    @Transactional
    void shouldFindDipendentiWithSalaryRange() {
        Dipendente manager = creaDipendente("Manager", "X", EmployeeRole.MANAGER);
        Dipendente d1 = creaDipendente("M", "N", EmployeeRole.JUNIOR);
        Dipendente d2 = creaDipendente("L", "O", EmployeeRole.JUNIOR);
        Dipendente d3 = creaDipendente("P", "Q", EmployeeRole.JUNIOR);

        List<Dipendente> equalThan = facade.findDipendentiWithSalaryGreaterThan(manager.getId(), EmployeeRole.JUNIOR.getMonthlySalary());
        assertEquals(1, equalThan.size());

        List<Dipendente> greaterThan = facade.findDipendentiWithSalaryGreaterThan(manager.getId(), 0.00);
        assertEquals(4, greaterThan.size());

        List<Dipendente> lessThan = facade.findDipendentiWithSalaryLessThan(manager.getId(), 0.00);
        assertEquals(0, lessThan.size());

        List<Dipendente> between = facade.findDipendentiWithSalaryBetween(manager.getId(), EmployeeRole.MANAGER.getMonthlySalary(), 100000.0);
        assertEquals(1, between.size());
        assertEquals(EmployeeRole.MANAGER.getMonthlySalary(), between.getFirst().getMonthlySalary());
    }

    // ========================= AGGIORNAMENTI =========================

    @Test
    void shouldUpdateSalaryAndRole() {
        Dipendente d = creaDipendente("Mario", "Rossi", EmployeeRole.JUNIOR);

        int updated = facade.updateEmployeeRoleAndMonthlySalary(d.getId(), 3000.0, EmployeeRole.SENIOR_SW_ENGINEER);
        assertEquals(1, updated);

        Dipendente updatedDip = facade.trovaPerIdDipendente(d.getId()).orElseThrow();
        assertEquals(3000.0, updatedDip.getMonthlySalary());
        assertEquals(EmployeeRole.SENIOR_SW_ENGINEER, updatedDip.getEmployeeRole());
    }

    // ========================= FIRE EMPLOYEE =========================

    @Test
    void shouldFireEmployee() {
        Dipendente manager = creaDipendente("Capo", "X", EmployeeRole.MANAGER);
        Dipendente dip = creaDipendente("Dipendente", "Y", EmployeeRole.JUNIOR);

        facade.eliminaPerIdDipendente(dip.getId());
        assertFalse(facade.esisteDipendentePerId(dip.getId()));
    }

    // ========================= TASKS =========================

    @Test
    @Transactional
    void shouldAssignAndFilterTasks() {
        Dipendente d = creaDipendente("Mario", "Rossi", EmployeeRole.JUNIOR);

        Task t1 = facade.saveTask(new Task());
        Task t2 = facade.saveTask(new Task());
        Task t3 = facade.saveTask(new Task());
        facade.assegnaDipendenteATask(t1.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t2.getIdTask(), d.getId());
        facade.assegnaDipendenteATask(t3.getIdTask(), d.getId());

        facade.cambiaStatoTask(t2.getIdTask(), TaskState.INIZIATO);
        facade.cambiaStatoTask(t2.getIdTask(), TaskState.FINITO);
        facade.cambiaStatoTask(t3.getIdTask(), TaskState.INIZIATO);

        assertEquals(1, facade.findTasksByDipendenteAndState(d.getId(), TaskState.INIZIATO).size());
        assertEquals(1, facade.findTasksByDipendenteAndState(d.getId(), TaskState.FINITO).size());
        assertEquals(1, facade.findTasksByDipendenteAndState(d.getId(), TaskState.DAINIZIARE).size());
    }

    // ========================= ALTRO =========================

    @Test
    void shouldReturnReferenceDipendente() {
        Dipendente d = creaDipendente("Ref", "Test", EmployeeRole.JUNIOR);
        Dipendente ref = facade.getReferenceDipendente(d.getId());
        assertEquals(d.getId(), ref.getId());
    }

    @Test
    void shouldCountDipendenti() {
        creaDipendente("R", "S", EmployeeRole.JUNIOR);
        creaDipendente("T", "U", EmployeeRole.JUNIOR);

        assertEquals(2, facade.contaTuttiDipendenti());
    }

    @Test
    void shouldDeleteAllDipendenti() {
        creaDipendente("Z", "X", EmployeeRole.JUNIOR);
        creaDipendente("Y", "P", EmployeeRole.JUNIOR);

        facade.eliminaTuttiDipendenti();
        assertTrue(facade.trovaTuttiDipendenti().isEmpty());
    }
}



