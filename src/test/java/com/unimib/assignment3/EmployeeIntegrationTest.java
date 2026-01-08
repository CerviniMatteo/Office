package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.enums.EmployeeRole;
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

        System.out.println(foundSalary);
        System.out.println(foundRole);
    }

    @Transactional
    @Test
    void shouldThrowIfNonManagerSearchBySalary() {
        Dipendente nonManager = createEmployee(EmployeeRole.JUNIOR);

        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeesByMonthlySalary(nonManager.getId(), 3000.0));
    }

}



