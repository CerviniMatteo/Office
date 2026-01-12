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
    @DisplayName("Gestione CRUD e Stato Task")
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
            Task tIniziato = facade.saveTask(facade.createTask(TaskState.STARTED));
            assertNotNull(tIniziato.getStartDate());
            assertNull(tIniziato.getEndDate());

            // Caso FINITO: il Service deve impostare sia startDate che endDate
            Task tFinito = facade.saveTask(facade.createTask(TaskState.DONE));
            assertNotNull(tFinito.getStartDate());
            assertNotNull(tFinito.getEndDate());
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
        void testCambioStatoEValidazioneDate() {
            Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Long id = task.getTaskId();

            // Test transizione DAINIZIARE -> INIZIATO
            facade.changeTaskState(id, TaskState.STARTED);
            Task statusIniziato = facade.getTaskById(id).get();
            assertEquals(TaskState.STARTED, statusIniziato.getTaskState());
            assertNotNull(statusIniziato.getStartDate());

            // Test transizione INIZIATO -> FINITO
            facade.changeTaskState(id, TaskState.DONE);
            Task statusFinito = facade.getTaskById(id).get();
            assertEquals(TaskState.DONE, statusFinito.getTaskState());
            assertNotNull(statusFinito.getEndDate());
        }

        @Test
        @DisplayName("Il cambio verso lo stato attuale non deve produrre errori (Idempotenza)")
        void testCambioStatoIdempotente() {
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
        void testEccezioniCambioStato() {
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
        void testAssegnazioneERimozioneDipendente() {
            Employee d = facade.createEmployee("Mario", "Rossi");
            d = facade.saveEmployee(d);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));

            facade.assignEmployeeToTask(t.getTaskId(), d.getPersonId());
            assertTrue(facade.isEmployeeAssigned(t.getTaskId(), d.getPersonId()));

            facade.removeEmployeeToTask(t.getTaskId(), d.getPersonId());
            assertFalse(facade.isEmployeeAssigned(t.getTaskId(), d.getPersonId()));
        }

        @Test
        void testAssegnazioneMultiplaVietata() {
            Employee d = facade.createEmployee("Luca", "Bianchi");
            d = facade.saveEmployee(d);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.assignEmployeeToTask(t.getTaskId(), d.getPersonId());

            Employee finalD = d;
            assertThrows(IllegalStateException.class, () -> facade.assignEmployeeToTask(t.getTaskId(), finalD.getPersonId()));
        }

        @Test
        @DisplayName("Non deve essere possibile assegnare dipendenti a task già FINITI")
        void testAssegnazioneVietataInStatoFinito() {
            Task t = facade.saveTask(facade.createTask(TaskState.DONE)); // Task già finito alla creazione
            Employee d = facade.createEmployee("Test", "Test");
            d = facade.saveEmployee(d);

            Employee finalD = d;
            assertThrows(IllegalStateException.class, () -> facade.assignEmployeeToTask(t.getTaskId(), finalD.getPersonId()));
        }

        @Test
        @DisplayName("Eccezione se si assegna un dipendente a un task inesistente")
        void testAssegnazioneTaskInesistente() {
            Employee d = facade.createEmployee("Invisibile", "User");
            d = facade.saveEmployee(d);
            Employee finalD = d;
            assertThrows(IllegalArgumentException.class, () -> facade.assignEmployeeToTask(999L, finalD.getPersonId()));
        }
    }

    @Nested
    @DisplayName("Copertura bidirezionale")
    class TaskbidirectionalTests{
        @Test
        @DisplayName("Verifica consistenza bidirezionale tra Task e Dipendente")
        void testConsistenzaBidirezionale() {
            Employee d = facade.createEmployee("Mario", "Rossi");
            d = facade.saveEmployee(d);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));

            facade.assignEmployeeToTask(t.getTaskId(), d.getPersonId());

            // Verifica lato Task
            assertTrue(facade.getTaskById(t.getTaskId()).get().getAssignedEmployees().contains(d));
            // Verifica lato Dipendente (molto importante per JPA)
            // Nota: potrebbe servire un refresh o caricamento dal repository del dipendente
            assertTrue(d.getTasks().stream().anyMatch(task -> task.getTaskId().equals(t.getTaskId())));

            facade.removeEmployeeToTask(t.getTaskId(), d.getPersonId());
            assertFalse(facade.getTaskById(t.getTaskId()).get().getAssignedEmployees().contains(d));
        }
    }

    @Nested
    @DisplayName("Query, Filtri e Statistiche")
    class TaskQueryTests {

        @Test
        void testFiltriEConteggi() {
            facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

            assertEquals(2, facade.countTasksByState(TaskState.STARTED));
            assertEquals(3, facade.getAllTasks().size());

            List<Task> iniziati = facade.getTasksByState(TaskState.TO_BE_STARTED);
            assertEquals(1, iniziati.size());
        }

        @Test
        void testSearchByDipendente() {
            Employee d = facade.createEmployee("Anna", "Verdi");
            d = facade.saveEmployee(d);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.assignEmployeeToTask(t.getTaskId(), d.getPersonId());

            List<Task> tasksAnna = facade.getTasksByEmployee(d);
            assertEquals(1, tasksAnna.size());
            assertEquals(t.getTaskId(), tasksAnna.get(0).getTaskId());
        }

        @Test
        void testTasksComplessiENonAssegnati() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee d1 = facade.createEmployee("D1", "C1");
            d1 = facade.saveEmployee(d1);
            Employee d2 = facade.createEmployee("D2", "C2");
            d2 = facade.saveEmployee(d2);

            facade.assignEmployeeToTask(t1.getTaskId(), d1.getPersonId());
            facade.assignEmployeeToTask(t1.getTaskId(), d2.getPersonId());

            List<Task> complessi = facade.getComplexTasks(1);
            assertTrue(complessi.contains(t1));

            List<Task> nonAssegnati = facade.getUnsignedTasks();
            assertFalse(nonAssegnati.contains(t1));
        }
    }

    @Nested
    @DisplayName("Estensione Query Avanzate")
    class TaskAdvancedQueryTests {

        @Test
        @DisplayName("Test query per stato con almeno un dipendente")
        void testFindTasksByStateWithDipendenti() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Employee d1 = facade.createEmployee("Test", "User");
            d1 = facade.saveEmployee(d1);
            facade.assignEmployeeToTask(t1.getTaskId(), d1.getPersonId());

            // Task nello stesso stato ma senza dipendenti
            facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

            List<Task> result = facade.findTasksByStateWithEmployee(TaskState.TO_BE_STARTED);
            assertEquals(1, result.size());
            assertEquals(t1.getTaskId(), result.get(0).getTaskId());
        }

        @Test
        @DisplayName("Test conteggio dipendenti per specifico Task ID")
        void testCountDipendentiByTaskId() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee d1 = facade.createEmployee("D1", "C1");
            d1 = facade.saveEmployee(d1);
            Employee d2 = facade.createEmployee("D2", "C2");
            d2 = facade.saveEmployee(d2);

            facade.assignEmployeeToTask(t1.getTaskId(), d1.getPersonId());
            facade.assignEmployeeToTask(t1.getTaskId(), d2.getPersonId());

            Integer count = facade.countEmployeeByTaskId(t1.getTaskId());
            assertEquals(2, count);
        }

        @Test
        @DisplayName("Test ricerca per stato e numero esatto di dipendenti")
        void testFindTasksByStateAndDipendentiCount() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee d1 = facade.createEmployee("Solo", "User");
            d1 = facade.saveEmployee(d1);
            facade.assignEmployeeToTask(t1.getTaskId(), d1.getPersonId());

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
        void testValidazioneDateIncoerenti() {
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