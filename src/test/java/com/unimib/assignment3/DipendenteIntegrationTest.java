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
    void pulisciDB() {
        facade.eliminaTuttiDipendenti();
        facade.deleteAllTasks();
    }

    private Dipendente creaDipendente(String nome, String cognome, double stipendio, EmployeeRole employeeRole) {
        Dipendente dipendente = new Dipendente(nome, cognome, stipendio, employeeRole);
        return facade.saveDipendente(dipendente);
    }

    @Test
    void testCreazioneDipendente() {
        Dipendente dip1 = creaDipendente("Matteo", "Cervini", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);
        Dipendente dip2 = creaDipendente("Andrea", "Aivaliotis", EmployeeRole.JUNIOR.getMonthlySalary(),EmployeeRole.JUNIOR);

        assertNotNull(dip1.getId());
        assertNotNull(dip2.getId());
        assertNotEquals(dip1.getId(), dip2.getId());
    }

    @Transactional
    @Test
    void testTrovaDipendentiPerStipendio() {
        Dipendente dip1 = creaDipendente("Matteo2", "Cervini", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.MANAGER);
        Dipendente dip2 = creaDipendente("Andrea2", "Aivaliotis", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.MANAGER);

        // nuovo metodo nel Facade per cercare per stipendio senza passare dipendente
        List<Dipendente> dipendentiJunior = facade.findDipendentiByStipendio(dip1, EmployeeRole.JUNIOR.getMonthlySalary());

        assertEquals(2, dipendentiJunior.size());
        dipendentiJunior.forEach(d -> assertEquals(EmployeeRole.JUNIOR.getMonthlySalary(), d.getMonthlySalary(), 0.01));
        assertEquals(dip1.getId(), dipendentiJunior.get(0).getId());
        assertEquals(dip2.getId(), dipendentiJunior.get(1).getId());

        Dipendente dip3 = creaDipendente("Matteo3", "Cervini", EmployeeRole.MANAGER.getMonthlySalary(), EmployeeRole.JUNIOR);
        List<Dipendente> dipendentiManager;
        try {
            dipendentiManager = facade.findDipendentiByStipendio(dip3, EmployeeRole.JUNIOR.getMonthlySalary());
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

        dip3.setEmployeeRole(EmployeeRole.MANAGER);
        dipendentiManager = facade.findDipendentiByStipendio(dip3, EmployeeRole.MANAGER.getMonthlySalary());
        assertEquals(1, dipendentiManager.size());
        assertEquals(dip3.getId(), dipendentiManager.getFirst().getId());

        List<Dipendente> dipendentiOverManager= facade.findDipendentiByStipendio(dip3, EmployeeRole.MANAGER.getMonthlySalary()+1000.00);
        assertTrue(dipendentiOverManager.isEmpty());
    }

    @Transactional
    @Test
    void testTrovaDipendentiPerGrado() {
        Dipendente dip1 = creaDipendente("Matteo4", "Cervini", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);
        Dipendente dip2 = creaDipendente("Andrea3", "Aivaliotis", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);

        List<Dipendente> junior = facade.findDipendentiByGrado(EmployeeRole.JUNIOR);
        assertEquals(2, junior.size());
        junior.forEach(d -> assertEquals(EmployeeRole.JUNIOR, d.getEmployeeRole()));
        assertEquals(dip1.getId(), junior.get(0).getId());
        assertEquals(dip2.getId(), junior.get(1).getId());

        Dipendente dip3 = creaDipendente("Matteo5", "Cervini", EmployeeRole.SENIOR_SW_ENGINEER.getMonthlySalary(), EmployeeRole.SENIOR_SW_ENGINEER);
        List<Dipendente> senior = facade.findDipendentiByGrado(EmployeeRole.SENIOR_SW_ENGINEER);
        assertEquals(1, senior.size());
        assertEquals(dip3.getId(), senior.getFirst().getId());
        assertEquals(EmployeeRole.SENIOR_SW_ENGINEER, senior.getFirst().getEmployeeRole());

        List<Dipendente> manager = facade.findDipendentiByGrado(EmployeeRole.MANAGER);
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
        Dipendente d = creaDipendente("Ref", "Test", 1500, EmployeeRole.JUNIOR);
        Dipendente ref = facade.getReferenceDipendente(d.getId());
        assertEquals(d.getId(), ref.getId());
    }

    @Test
    void testContaTuttiDipendenti() {
        creaDipendente("A", "B", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);
        creaDipendente("C", "D", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);
        long count = facade.contaTuttiDipendenti();
        assertEquals(2, count);
    }

    @Test
    void testEliminaTuttiDipendenti() {
        Dipendente d1 = creaDipendente("A", "B", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);
        Dipendente d2 = creaDipendente("C", "D", EmployeeRole.JUNIOR.getMonthlySalary(), EmployeeRole.JUNIOR);
        facade.saveAllDipendenti(List.of(d1, d2));
        facade.eliminaTuttiDipendenti();
        assertEquals(0, facade.trovaTuttiDipendenti().size());
    }

}


