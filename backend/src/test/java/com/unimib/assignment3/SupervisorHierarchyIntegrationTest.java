package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
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

    /**
     * Helper method to create and save a supervisor via the facade.
     */
    private Supervisor createSupervisor() {
        return facade.createSupervisor("Matteo", "Cervini");
    }

    @Test
    void supervisoreIsAnEmployee() {
        Supervisor supervisor = createSupervisor();
        supervisor = facade.saveSupervisor(supervisor);

        assertInstanceOf(Employee.class, supervisor);
    }

    @Test
    void supervisoreHasInheritedProperties() {
        Supervisor supervisor = createSupervisor();
        supervisor = facade.saveSupervisor(supervisor);

        assertTrue(supervisor.getName().contains("Matteo"));
        assertEquals("Cervini", supervisor.getSurname());
        assertNotNull(supervisor.getEmployeeRole());
    }
}
