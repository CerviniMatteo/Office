package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
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
public class FacadeTaskTest {

    @Autowired
    private Facade facade;

    @Nested
    @DisplayName("Gestione CRUD e Stato Task")
    class TaskCoreTests {

        @Test
        void testCreateAndSaveTask() {
            Task task = new Task();
            Task saved = facade.saveTask(task);
            assertNotNull(saved.getIdTask());

            Optional<Task> found = facade.getTaskById(saved.getIdTask());
            assertTrue(found.isPresent());
        }

        @Test
        @DisplayName("Verifica creazione con stati iniziali differenti e impostazione date")
        void testCreateTaskWithInitialState() {
            // Caso INIZIATO: il Service deve impostare la startDate
            Task tIniziato = facade.createTask(TaskState.INIZIATO);
            assertNotNull(tIniziato.getStartDate());
            assertNull(tIniziato.getEndDate());

            // Caso FINITO: il Service deve impostare sia startDate che endDate
            Task tFinito = facade.createTask(TaskState.FINITO);
            assertNotNull(tFinito.getStartDate());
            assertNotNull(tFinito.getEndDate());
        }
        @Test
        @DisplayName("Verifica che createTask(null) imposti lo stato di default")
        void testCreateTaskNullState() {
            Task t = facade.createTask(null);
            assertEquals(TaskState.DAINIZIARE, t.getTaskState());
            assertNull(t.getStartDate());
        }

        @Test
        void testDeleteTask() {
            Task task = facade.createTask(TaskState.DAINIZIARE);
            Long id = task.getIdTask();
            facade.deleteTask(id);
            assertTrue(facade.getTaskById(id).isEmpty());
        }

        @Test
        void testCambioStatoEValidazioneDate() {
            Task task = facade.createTask(TaskState.DAINIZIARE);
            Long id = task.getIdTask();

            // Test transizione DAINIZIARE -> INIZIATO
            facade.cambiaStatoTask(id, TaskState.INIZIATO);
            Task statusIniziato = facade.getTaskById(id).get();
            assertEquals(TaskState.INIZIATO, statusIniziato.getTaskState());
            assertNotNull(statusIniziato.getStartDate());

            // Test transizione INIZIATO -> FINITO
            facade.cambiaStatoTask(id, TaskState.FINITO);
            Task statusFinito = facade.getTaskById(id).get();
            assertEquals(TaskState.FINITO, statusFinito.getTaskState());
            assertNotNull(statusFinito.getEndDate());
        }

        @Test
        @DisplayName("Il cambio verso lo stato attuale non deve produrre errori (Idempotenza)")
        void testCambioStatoIdempotente() {
            Task task = facade.createTask(TaskState.INIZIATO);
            Long id = task.getIdTask();

            assertDoesNotThrow(() -> facade.cambiaStatoTask(id, TaskState.INIZIATO));
            assertEquals(TaskState.INIZIATO, facade.getTaskById(id).get().getTaskState());
        }

        @Test
        @DisplayName("Verifica il reset del task allo stato iniziale e pulizia date")
        void testResetTask() {
            Task t = facade.createTask(TaskState.INIZIATO);
            facade.cambiaStatoTask(t.getIdTask(), TaskState.FINITO);

            facade.resetTask(t.getIdTask()); // Copre resetTask
            Task reset = facade.getTaskById(t.getIdTask()).get();

            assertEquals(TaskState.DAINIZIARE, reset.getTaskState());
            assertNull(reset.getStartDate());
            assertNull(reset.getEndDate());
        }

        @Test
        void testEccezioniCambioStato() {
            Task task = facade.createTask(TaskState.DAINIZIARE);
            // Salto vietato: DAINIZIARE -> FINITO
            assertThrows(IllegalStateException.class, () -> facade.cambiaStatoTask(task.getIdTask(), TaskState.FINITO));

            facade.cambiaStatoTask(task.getIdTask(), TaskState.INIZIATO);
            facade.cambiaStatoTask(task.getIdTask(), TaskState.FINITO);
            // Modifica vietata se FINITO
            assertThrows(IllegalStateException.class, () -> facade.cambiaStatoTask(task.getIdTask(), TaskState.INIZIATO));
        }
    }

    @Nested
    @DisplayName("Gestione Assegnazioni e Relazioni")
    class TaskAssignmentTests {

        @Test
        void testAssegnazioneERimozioneDipendente() {
            Dipendente d = facade.saveDipendente(new Dipendente("Mario", "Rossi"));
            Task t = facade.createTask(TaskState.DAINIZIARE);

            facade.assegnaDipendenteATask(t.getIdTask(), d.getId());
            assertTrue(facade.isDipendenteAssegnato(t.getIdTask(), d.getId()));

            facade.rimuoviDipendenteDaTask(t.getIdTask(), d.getId());
            assertFalse(facade.isDipendenteAssegnato(t.getIdTask(), d.getId()));
        }

        @Test
        void testAssegnazioneMultiplaVietata() {
            Dipendente d = facade.saveDipendente(new Dipendente("Luca", "Bianchi"));
            Task t = facade.createTask(TaskState.DAINIZIARE);
            facade.assegnaDipendenteATask(t.getIdTask(), d.getId());

            assertThrows(IllegalStateException.class, () -> facade.assegnaDipendenteATask(t.getIdTask(), d.getId()));
        }

        @Test
        @DisplayName("Non deve essere possibile assegnare dipendenti a task già FINITI")
        void testAssegnazioneVietataInStatoFinito() {
            Task t = facade.createTask(TaskState.FINITO); // Task già finito alla creazione
            Dipendente d = facade.saveDipendente(new Dipendente("Test", "Test"));

            assertThrows(IllegalStateException.class, () -> facade.assegnaDipendenteATask(t.getIdTask(), d.getId()));
        }

        @Test
        @DisplayName("Eccezione se si assegna un dipendente a un task inesistente")
        void testAssegnazioneTaskInesistente() {
            Dipendente d = facade.saveDipendente(new Dipendente("Invisibile", "User"));
            assertThrows(IllegalArgumentException.class, () -> facade.assegnaDipendenteATask(999L, d.getId()));
        }
    }

    @Nested
    @DisplayName("Copertura bidirezionale")
    class TaskbidirectionalTests{
        @Test
        @DisplayName("Verifica consistenza bidirezionale tra Task e Dipendente")
        void testConsistenzaBidirezionale() {
            Dipendente d = facade.saveDipendente(new Dipendente("Mario", "Rossi"));
            Task t = facade.createTask(TaskState.DAINIZIARE);

            facade.assegnaDipendenteATask(t.getIdTask(), d.getId());

            // Verifica lato Task
            assertTrue(facade.getTaskById(t.getIdTask()).get().getDipendentiAssegnati().contains(d));
            // Verifica lato Dipendente (molto importante per JPA)
            // Nota: potrebbe servire un refresh o caricamento dal repository del dipendente
            assertTrue(d.getTasks().stream().anyMatch(task -> task.getIdTask().equals(t.getIdTask())));

            facade.rimuoviDipendenteDaTask(t.getIdTask(), d.getId());
            assertFalse(facade.getTaskById(t.getIdTask()).get().getDipendentiAssegnati().contains(d));
        }
    }

    @Nested
    @DisplayName("Query, Filtri e Statistiche")
    class TaskQueryTests {

        @Test
        void testFiltriEConteggi() {
            facade.createTask(TaskState.DAINIZIARE);
            facade.createTask(TaskState.DAINIZIARE);
            facade.createTask(TaskState.INIZIATO);

            assertEquals(2, facade.countTasksByStato(TaskState.DAINIZIARE));
            assertEquals(3, facade.getAllTasks().size());

            List<Task> iniziati = facade.getTasksByStato(TaskState.INIZIATO);
            assertEquals(1, iniziati.size());
        }

        @Test
        void testSearchByDipendente() {
            Dipendente d = facade.saveDipendente(new Dipendente("Anna", "Verdi"));
            Task t = facade.createTask(TaskState.DAINIZIARE);
            facade.assegnaDipendenteATask(t.getIdTask(), d.getId());

            List<Task> tasksAnna = facade.getTasksByDipendente(d);
            assertEquals(1, tasksAnna.size());
            assertEquals(t.getIdTask(), tasksAnna.get(0).getIdTask());
        }

        @Test
        void testTasksComplessiENonAssegnati() {
            Task t1 = facade.createTask(TaskState.DAINIZIARE);
            Dipendente d1 = facade.saveDipendente(new Dipendente("D1", "C1"));
            Dipendente d2 = facade.saveDipendente(new Dipendente("D2", "C2"));

            facade.assegnaDipendenteATask(t1.getIdTask(), d1.getId());
            facade.assegnaDipendenteATask(t1.getIdTask(), d2.getId());

            List<Task> complessi = facade.getTasksComplessi(1);
            assertTrue(complessi.contains(t1));

            List<Task> nonAssegnati = facade.getTasksNonAssegnati();
            assertFalse(nonAssegnati.contains(t1));
        }
    }

    @Nested
    @DisplayName("Estensione Query Avanzate")
    class TaskAdvancedQueryTests {

        @Test
        @DisplayName("Test query per stato con almeno un dipendente")
        void testFindTasksByStateWithDipendenti() {
            Task t1 = facade.createTask(TaskState.INIZIATO);
            Dipendente d1 = facade.saveDipendente(new Dipendente("Test", "User"));
            facade.assegnaDipendenteATask(t1.getIdTask(), d1.getId());

            // Task nello stesso stato ma senza dipendenti
            facade.createTask(TaskState.INIZIATO);

            List<Task> result = facade.findTasksByStateWithDipendenti(TaskState.INIZIATO);
            assertEquals(1, result.size());
            assertEquals(t1.getIdTask(), result.get(0).getIdTask());
        }

        @Test
        @DisplayName("Test conteggio dipendenti per specifico Task ID")
        void testCountDipendentiByTaskId() {
            Task t1 = facade.createTask(TaskState.DAINIZIARE);
            Dipendente d1 = facade.saveDipendente(new Dipendente("D1", "C1"));
            Dipendente d2 = facade.saveDipendente(new Dipendente("D2", "C2"));

            facade.assegnaDipendenteATask(t1.getIdTask(), d1.getId());
            facade.assegnaDipendenteATask(t1.getIdTask(), d2.getId());

            Integer count = facade.countDipendentiByTaskId(t1.getIdTask());
            assertEquals(2, count);
        }

        @Test
        @DisplayName("Test ricerca per stato e numero esatto di dipendenti")
        void testFindTasksByStateAndDipendentiCount() {
            Task t1 = facade.createTask(TaskState.DAINIZIARE);
            Dipendente d1 = facade.saveDipendente(new Dipendente("Solo", "User"));
            facade.assegnaDipendenteATask(t1.getIdTask(), d1.getId());

            List<Task> result = facade.findTasksByStateAndDipendentiCount(TaskState.DAINIZIARE, 1);
            assertEquals(1, result.size());
            assertTrue(result.contains(t1));
        }

        @Test
        @DisplayName("Test ricerca Task per Team ID")
        void testFindTasksByTeamId() {
            // Creazione Team e Task
            Supervisore s = facade.saveSupervisore(new Supervisore("Boss", "Generale"));
            Team team = new Team(s);
            Task t1 = facade.createTask(TaskState.DAINIZIARE);

            // Colleghiamo il task al team (Verifica i metodi in Team.java)
            team.setTasksTeam(List.of(t1));
            Team savedTeam = facade.saveTeam(team);

            List<Task> tasksDelTeam = facade.findTasksByTeamId(savedTeam.getIdTeam());
            assertFalse(tasksDelTeam.isEmpty());
            assertEquals(t1.getIdTask(), tasksDelTeam.get(0).getIdTask());
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