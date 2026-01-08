package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeIntegrationTest {

    @Autowired
    private Facade facade;

    private static long counter = 0;

    private Dipendente createEmployee(EmployeeRole role) {
        counter++;

        return facade.saveEmployee(
                new Dipendente(
                        "Nome" + counter,
                        "Cognome" + counter,
                        role.getMonthlySalary(),
                        role
                )
        );
    }

    private Dipendente createEmployee(EmployeeRole role, Double salary) {
        counter++;

        return facade.saveEmployee(
                new Dipendente(
                        "Nome" + counter,
                        "Cognome" + counter,
                        salary,
                        role
                )
        );
    }

    @Transactional
    @Test
    void shouldFindEmployeeById() {
        Dipendente d = createEmployee(EmployeeRole.JUNIOR);

        assertTrue(facade.findEmployeeById(d.getId()).isPresent());
    }

    @Transactional
    @Test
    void shouldFindAllEmployees() {
        createEmployee(EmployeeRole.JUNIOR);
        createEmployee(EmployeeRole.SENIOR_SW_ENGINEER);

        List<Dipendente> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2);
    }

    @Transactional
    @Test
    void shouldDeleteEmployeeByManager() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        facade.fireEmployee(manager.getId(), employee.getId());

        assertTrue(facade.findEmployeeById(employee.getId()).isEmpty());
    }

    @Transactional
    @Test
    void shouldThrowIfFireEmployeeByNonManager() {
        Dipendente nonManager = createEmployee(EmployeeRole.JUNIOR);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        assertThrows(IllegalArgumentException.class,
                () -> facade.fireEmployee(nonManager.getId(), employee.getId()));
    }

    @Transactional
    @Test
    void shouldFireMultipleEmployees() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR);
        Dipendente e2 = createEmployee(EmployeeRole.JUNIOR);

        facade.fireEmployees(manager.getId(), List.of(e1, e2));

        assertTrue(facade.findEmployeeById(e1.getId()).isEmpty());
        assertTrue(facade.findEmployeeById(e2.getId()).isEmpty());
    }

    @Transactional
    @Test
    void shouldUpdateMonthlySalary() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        facade.updateMonthlySalaryById(
                manager.getId(),
                employee.getId(),
                5000.0
        );

        Dipendente updated = facade.findEmployeeById(employee.getId()).get();
        assertEquals(5000.0, updated.getMonthlySalary());
    }

    @Transactional
    @Test
    void shouldUpdateEmployeeRole() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        facade.updateEmployeeRoleById(
                manager.getId(),
                employee.getId(),
                EmployeeRole.SENIOR_SW_ENGINEER
        );

        Dipendente updated = facade.findEmployeeById(employee.getId()).get();
        assertEquals(EmployeeRole.SENIOR_SW_ENGINEER, updated.getEmployeeRole());
    }

    @Transactional
    @Test
    void shouldThrowIfUpdateByNonManager() {
        Dipendente nonManager = createEmployee(EmployeeRole.JUNIOR);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        assertThrows(IllegalArgumentException.class,
                () -> facade.updateMonthlySalaryById(
                        nonManager.getId(),
                        employee.getId(),
                        4000.0
                ));
    }

    @Transactional
    @Test
    void shouldThrowIfEmployeeNotFound() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);

        assertThrows(IllegalArgumentException.class,
                () -> facade.updateEmployeeRoleById(
                        manager.getId(),
                        999L,
                        EmployeeRole.JUNIOR
                ));
    }

    @Transactional
    @Test
    void shouldFindEmployeesByMonthlySalary() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);

        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR);
        facade.updateMonthlySalaryById(manager.getId(), e1.getId(), 3100.0);

        Dipendente e2 = createEmployee(EmployeeRole.MANAGER);
        facade.updateMonthlySalaryById(manager.getId(), e2.getId(), 3100.0);

        List<Dipendente> foundSalary = facade.findEmployeesByMonthlySalary(manager.getId(), 3100.0);
        List<Dipendente> foundRole = facade.findEmployeesByEmployeeRole(manager.getId(), EmployeeRole.MANAGER);

        assertEquals(2, foundSalary.size());
        assertEquals(2, foundRole.size());
        assertTrue(foundSalary.stream().allMatch(d -> d.getMonthlySalary().equals(3100.0)));
        assertTrue(foundRole.stream().allMatch(d -> d.getEmployeeRole().equals(EmployeeRole.MANAGER)));
    }

    @Transactional
    @Test
    void shouldThrowIfNonManagerSearchBySalary() {
        Dipendente nonManager = createEmployee(EmployeeRole.JUNIOR);

        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeesByMonthlySalary(nonManager.getId(), 3000.0));
    }


    @Transactional
    @Test
    void shouldSortEmployeesByMonthlySalaryAscAndDesc() {
        // Manager per autorizzazioni
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);

        // Dipendenti con salari diversi
        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR, 1500.0);
        Dipendente e2 = createEmployee(EmployeeRole.SENIOR, 1500.0);
        Dipendente e3 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER, 1500.0);

        // Aggiorniamo salari tramite service
        facade.updateMonthlySalaryById(manager.getId(), e1.getId(), e1.getMonthlySalary());
        facade.updateMonthlySalaryById(manager.getId(), e2.getId(), e2.getMonthlySalary());
        facade.updateMonthlySalaryById(manager.getId(), e3.getId(), e3.getMonthlySalary());

        // Ordinamento ascendente
        List<Dipendente> asc = facade.findEmployeesByMonthlySalaryAscByEmployeeRole(manager.getId(), 1500.0);
        List<EmployeeRole> ascSalaries = asc.stream().map(Dipendente::getEmployeeRole).toList();
        assertEquals(List.of(EmployeeRole.JUNIOR, EmployeeRole.SENIOR, EmployeeRole.SENIOR_SW_ENGINEER), ascSalaries);

        // Ordinamento discendente
        List<Dipendente> desc = facade.findEmployeesByMonthlySalaryDescByEmployeeRole(manager.getId(), 1500.0);
        List<EmployeeRole> descSalaries = desc.stream().map(Dipendente::getEmployeeRole).toList();
        assertEquals(List.of(EmployeeRole.SENIOR_SW_ENGINEER, EmployeeRole.SENIOR, EmployeeRole.JUNIOR), descSalaries);
    }


    @Transactional
    @Test
    void shouldSortEmployeesByRoleAscAndDesc() {
        // Manager per autorizzazioni
        Dipendente manager = createEmployee(EmployeeRole.MANAGER, 5000.0);

        // Dipendenti con ruoli diversi e salari diversi
        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR, 2000.0);
        Dipendente e2 = createEmployee(EmployeeRole.JUNIOR, 3000.0);
        Dipendente e3 = createEmployee(EmployeeRole.JUNIOR, 6000.0);
        Dipendente e4 = createEmployee(EmployeeRole.JUNIOR, 4000.0);

        // Ordinamento per ruolo ascendente (poi eventuale tie-break su stipendio)
        List<Dipendente> roleAsc = facade.findEmployeesByEmployeeRoleAscByMonthlySalary(manager.getId(), EmployeeRole.JUNIOR);
        List<Double> roleAscSalaries = roleAsc.stream().map(Dipendente::getMonthlySalary).toList();
        // Tutti JUNIOR -> ordinamento su stipendio crescente
        assertFalse(roleAscSalaries.isEmpty());
        assertEquals(List.of(2000.0,3000.0,4000.0,6000.0), roleAscSalaries);

        // Ordinamento per ruolo discendente
        List<Dipendente> roleDesc = facade.findEmployeesByEmployeeRoleDescByMonthlySalary(manager.getId(), EmployeeRole.JUNIOR);
        List<Double> roleDescSalaries = roleDesc.stream().map(Dipendente::getMonthlySalary).toList();
        // Tutti JUNIOR -> ordinamento su stipendio decrescente
        assertFalse(roleDescSalaries.isEmpty());
        assertEquals(List.of(6000.0,4000.0,3000.0,2000.0), roleDescSalaries);
    }

    @Transactional
    @Test
    void testFindTaskByDipendenteAndState() {
        Dipendente e = createEmployee(EmployeeRole.JUNIOR);

        Task t1 = facade.saveTask(new Task());
        Task t2 = facade.saveTask(new Task());
        Task t3 = facade.saveTask(new Task());
        t3.setTaskState(TaskState.INIZIATO);
        Task t4 = facade.saveTask(new Task());

        // assegna task usando facade
        facade.assegnaDipendenteATask(t1.getIdTask(), e.getId());
        facade.assegnaDipendenteATask(t2.getIdTask(), e.getId());
        facade.assegnaDipendenteATask(t3.getIdTask(), e.getId());
        facade.assegnaDipendenteATask(t4.getIdTask(), e.getId());
        t4.setTaskState(TaskState.FINITO);

        List<Task> openTasks = facade.findTasksByEmployeeAndTaskState(e.getId(), TaskState.DAINIZIARE);
        assertEquals(2, openTasks.size());
        openTasks.forEach(t -> assertEquals(TaskState.DAINIZIARE, t.getTaskState()));

        List<Task> startedTasks = facade.findTasksByEmployeeAndTaskState(e.getId(), TaskState.INIZIATO);
        assertEquals(1, startedTasks.size());
        assertEquals(TaskState.INIZIATO, startedTasks.getFirst().getTaskState());

        List<Task> endedTasks = facade.findTasksByEmployeeAndTaskState(e.getId(), TaskState.FINITO);
        assertEquals(1, endedTasks.size());
        assertEquals(TaskState.FINITO, endedTasks.getFirst().getTaskState());
    }

}