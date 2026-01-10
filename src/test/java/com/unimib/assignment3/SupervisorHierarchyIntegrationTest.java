package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class SupervisorHierarchyIntegrationTest {
    @Autowired
    private Facade facade;
    private static long counter = 0;

    /**
     * Helper method to create and save a supervisor via the facade.
     */
    private Supervisore createSupervisor() {
        counter++;
        return facade.createSupervisor("Matteo"+counter, "Cervini");
    }
    private Supervisore createSupervisor(String name, String surname, double monthlySalary, EmployeeRole employeeRole) {
        counter++;
        return facade.createSupervisor(name+counter, surname, monthlySalary, employeeRole);
    }

    @Test
    void supervisoreIsAnEmployee() {
        Supervisore supervisor = createSupervisor();
        supervisor = facade.saveSupervisor(supervisor);

        assertInstanceOf(Dipendente.class, supervisor);
    }

    @Test
    void supervisoreHasInheritedProperties() {
        Supervisore supervisor = createSupervisor();
        supervisor = facade.saveSupervisor(supervisor);

        assertTrue(supervisor.getNome().contains("Matteo"));
        assertEquals("Cervini", supervisor.getCognome());
        assertNotNull(supervisor.getMonthlySalary());
        assertNotNull(supervisor.getEmployeeRole());
    }
}
