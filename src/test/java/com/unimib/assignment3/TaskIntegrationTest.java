package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TaskIntegrationTest {

    @Autowired
    private Facade facade;

    @Nested
    @DisplayName("CRUD and Task State management")
    class TaskCoreTests {

        @Test
        void testCreateAndSaveTask() {
            Task task = new Task();
            Task saved = facade.saveTask(task);
            assertNotNull(saved.getTaskId());

            Optional<Task> found = facade.getTaskById(saved.getTaskId());
            assertTrue(found.isPresent());
        }

        @Test
        @DisplayName("Verifica creazione con stati iniziali differenti e impostazione date")
        void testCreateTaskWithInitialState() {
            // Caso INIZIATO: il Service deve impostare la startDate
            Task tStarted = facade.saveTask(facade.createTask(TaskState.STARTED));
            assertNotNull(tStarted.getStartDate());
            assertNull(tStarted.getEndDate());

            // Caso FINITO: il Service deve impostare sia startDate che endDate
            Task tDone = facade.saveTask(facade.createTask(TaskState.DONE));
            assertNotNull(tDone.getStartDate());
            assertNotNull(tDone.getEndDate());
        }
        @Test
        @DisplayName("Verifica che createTask(null) imposti lo stato di default")
        void testCreateTaskNullState() {
            Task t = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            assertEquals(TaskState.TO_BE_STARTED, t.getTaskState());
            assertNull(t.getStartDate());
        }

        @Test
        void testDeleteTask() {
            Task task = facade.saveTask(facade.createTask(TaskState.STARTED));
            Long id = task.getTaskId();
            facade.deleteTask(id);
            assertTrue(facade.getTaskById(id).isEmpty());
        }

        @Test
        void testStateChangeAndDateValidation() {
            Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Long id = task.getTaskId();

            // Test transizione DAINIZIARE -> INIZIATO
            facade.changeTaskState(id, TaskState.STARTED);
            Task statusStarted = facade.getTaskById(id).get();
            assertEquals(TaskState.STARTED, statusStarted.getTaskState());
            assertNotNull(statusStarted.getStartDate());

            // Test transizione INIZIATO -> FINITO
            facade.changeTaskState(id, TaskState.DONE);
            Task statusDone = facade.getTaskById(id).get();
            assertEquals(TaskState.DONE, statusDone.getTaskState());
            assertNotNull(statusDone.getEndDate());
        }

        @Test
        @DisplayName("Il cambio verso lo stato attuale non deve produrre errori (Idempotenza)")
        void testIdempotentStateChange() {
            Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Long id = task.getTaskId();

            assertDoesNotThrow(() -> facade.changeTaskState(id, TaskState.TO_BE_STARTED));
            assertEquals(TaskState.TO_BE_STARTED, facade.getTaskById(id).get().getTaskState());
        }

        @Test
        @DisplayName("Verifica il reset del task allo stato iniziale e pulizia date")
        void testResetTask() {
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.changeTaskState(t.getTaskId(), TaskState.DONE);

            facade.resetTask(t.getTaskId()); // Copre resetTask
            Task reset = facade.getTaskById(t.getTaskId()).get();

            assertEquals(TaskState.TO_BE_STARTED, reset.getTaskState());
            assertNull(reset.getStartDate());
            assertNull(reset.getEndDate());
        }

        @Test
        void testStateChangeException() {
            Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            // Salto vietato: DAINIZIARE -> FINITO
            assertThrows(IllegalStateException.class, () -> facade.changeTaskState(task.getTaskId(), TaskState.DONE));

            facade.changeTaskState(task.getTaskId(), TaskState.STARTED);
            facade.changeTaskState(task.getTaskId(), TaskState.DONE);
            // Modifica vietata se FINITO
            assertThrows(IllegalStateException.class, () -> facade.changeTaskState(task.getTaskId(), TaskState.TO_BE_STARTED));
        }
    }

    @Nested
    @DisplayName("Gestione Assegnazioni e Relazioni")
    class TaskAssignmentTests {

        @Test
        void testEmployeeAssignmentAndRemoval() {
            Employee employee = facade.createEmployee("Mario", "Rossi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));

            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());
            assertTrue(facade.isEmployeeAssigned(t.getTaskId(), employee.getPersonId()));

            facade.removeEmployeeToTask(t.getTaskId(), employee.getPersonId());
            assertFalse(facade.isEmployeeAssigned(t.getTaskId(), employee.getPersonId()));
        }

        @Test
        void testMultipleAssignmentForbidden() {
            Employee employee = facade.createEmployee("Luca", "Bianchi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());

            Employee finalEmployee = employee;
            assertThrows(IllegalStateException.class, () -> facade.assignEmployeeToTask(t.getTaskId(), finalEmployee.getPersonId()));
        }

        @Test
        @DisplayName("Non deve essere possibile assegnare dipendenti a task già FINITI")
        void testAssignmentForbiddenInFinalState() {
            Task t = facade.saveTask(facade.createTask(TaskState.DONE)); // Task già finito alla creazione
            Employee employee = facade.createEmployee("Test", "Test");
            employee = facade.saveEmployee(employee);

            Employee finalEmployee = employee;
            assertThrows(IllegalStateException.class, () -> facade.assignEmployeeToTask(t.getTaskId(), finalEmployee.getPersonId()));
        }

        @Test
        @DisplayName("Eccezione se si assegna un dipendente a un task inesistente")
        void testAssignmentToNonExistentTask() {
            Employee employee = facade.createEmployee("Invisibile", "User");
            employee = facade.saveEmployee(employee);
            Employee finalEmployee = employee;
            assertThrows(IllegalArgumentException.class, () -> facade.assignEmployeeToTask(999L, finalEmployee.getPersonId()));
        }
    }

    @Nested
    @DisplayName("Copertura bidirezionale")
    class TaskBidirectionalTests {
        @Test
        @DisplayName("Verifica consistenza bidirezionale tra Task e Dipendente")
        void testBidirectionalConsistency() {
            Employee employee = facade.createEmployee("Mario", "Rossi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));

            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());

            // Verifica lato Task
            assertTrue(facade.getTaskById(t.getTaskId()).get().getAssignedEmployees().contains(employee));
            // Verifica lato Dipendente (molto importante per JPA)
            // Nota: potrebbe servire un refresh o caricamento dal repository del dipendente
            assertTrue(employee.getTasks().stream().anyMatch(task -> task.getTaskId().equals(t.getTaskId())));

            facade.removeEmployeeToTask(t.getTaskId(), employee.getPersonId());
            assertFalse(facade.getTaskById(t.getTaskId()).get().getAssignedEmployees().contains(employee));
        }
    }

    @Nested
    @DisplayName("Query, Filtri e Statistiche")
    class TaskQueryTests {

        @Test
        void testFiltersAndCounts() {
            facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

            assertEquals(2, facade.countTasksByState(TaskState.STARTED));
            assertEquals(3, facade.getAllTasks().size());

            List<Task> startedTasks = facade.getTasksByState(TaskState.TO_BE_STARTED);
            assertEquals(1, startedTasks.size());
        }

        @Test
        void testSearchByEmployee() {
            Employee employee = facade.createEmployee("Anna", "Verdi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());

            List<Task> tasksAnna = facade.getTasksByEmployee(employee);
            assertEquals(1, tasksAnna.size());
            assertEquals(t.getTaskId(), tasksAnna.get(0).getTaskId());
        }

        @Test
        void testComplexAndUnassignedTasks() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee employee1 = facade.createEmployee("D1", "C1");
            employee1 = facade.saveEmployee(employee1);
            Employee employee2 = facade.createEmployee("D2", "C2");
            employee2 = facade.saveEmployee(employee2);

            facade.assignEmployeeToTask(t1.getTaskId(), employee1.getPersonId());
            facade.assignEmployeeToTask(t1.getTaskId(), employee2.getPersonId());

            List<Task> complexTasks = facade.getComplexTasks(1);
            assertTrue(complexTasks.contains(t1));

            List<Task> unassignedTasks = facade.getUnsignedTasks();
            assertFalse(unassignedTasks.contains(t1));
        }
    }

    @Nested
    @DisplayName("Estensione Query Avanzate")
    class TaskAdvancedQueryTests {

        @Test
        @DisplayName("Test query per stato con almeno un dipendente")
        void testFindTasksByStateWithEmployees() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Employee employee1 = facade.createEmployee("Test", "User");
            employee1 = facade.saveEmployee(employee1);
            facade.assignEmployeeToTask(t1.getTaskId(), employee1.getPersonId());

            // Task nello stesso stato ma senza dipendenti
            facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

            List<Task> result = facade.findTasksByStateWithEmployee(TaskState.TO_BE_STARTED);
            assertEquals(1, result.size());
            assertEquals(t1.getTaskId(), result.get(0).getTaskId());
        }

        @Test
        @DisplayName("Test conteggio dipendenti per specifico Task ID")
        void testCountEmployeesByTaskId() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee employee1 = facade.createEmployee("D1", "C1");
            employee1 = facade.saveEmployee(employee1);
            Employee employee2 = facade.createEmployee("D2", "C2");
            employee2 = facade.saveEmployee(employee2);

            facade.assignEmployeeToTask(t1.getTaskId(), employee1.getPersonId());
            facade.assignEmployeeToTask(t1.getTaskId(), employee2.getPersonId());

            Integer count = facade.countEmployeeByTaskId(t1.getTaskId());
            assertEquals(2, count);
        }

        @Test
        @DisplayName("Test ricerca per stato e numero esatto di dipendenti")
        void testFindTasksByStateAndEmployeesCount() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee employee = facade.createEmployee("Solo", "User");
            employee = facade.saveEmployee(employee);
            facade.assignEmployeeToTask(t1.getTaskId(), employee.getPersonId());

            List<Task> result = facade.findTasksByStateAndCountEmployee(TaskState.STARTED, 1);
            assertEquals(1, result.size());
            assertTrue(result.contains(t1));
        }

        @Test
        @DisplayName("Test ricerca Task per Team ID")
        void testFindTasksByTeamId() {
            // Creazione Team e Task
            Supervisor s = facade.createSupervisor("Boss", "Generale");
            s = facade.saveSupervisor(s);
            Team team = new Team(s);
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));

            // Colleghiamo il task al team (Verifica i metodi in Team.java)
            team.addTask(t1);
            Team savedTeam = facade.saveTeam(team);

            List<Task> tasksDelTeam = facade.findTasksByTeamId(savedTeam.getTeamId());
            assertFalse(tasksDelTeam.isEmpty());
            assertEquals(t1.getTaskId(), tasksDelTeam.getFirst().getTaskId());
        }
    }

    @Nested
    @DisplayName("Validazioni Logica Interna (POJO)")
    class TaskPojoTests {
        @Test
        @DisplayName("Verifica che il POJO impedisca date incoerenti")
        void testInconsistentDateValidation() {
            Task t = new Task();
            t.setEndDate(LocalDate.now());

            // Provare a mettere start date dopo end date deve lanciare eccezione
            assertThrows(IllegalArgumentException.class, () -> t.setStartDate(LocalDate.now().plusDays(1)));

            Task t2 = new Task();
            t2.setStartDate(LocalDate.now());
            // Provare a mettere end date prima di start date deve lanciare eccezione
            assertThrows(IllegalArgumentException.class, () -> t2.setEndDate(LocalDate.now().minusDays(1)));
        }
    }


}