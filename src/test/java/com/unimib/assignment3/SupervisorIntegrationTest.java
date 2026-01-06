package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SupervisorIntegrationTest {

    @Autowired
    private Facade facade;

    private Supervisore creaSupervisore(String nome, String cognome) {
        return facade.saveSupervisore(new Supervisore(nome, cognome));
    }

    @Test
    @Transactional
    void shouldCreateSupervisorsAndFindById() {
        // Arrange
        Supervisore supervisor1 = creaSupervisore("Matteo", "Cervini");
        Supervisore supervisor2 = creaSupervisore("Andrea", "Aivaliotis");
        Supervisore boss = creaSupervisore("Le Yang", "Shi");

        // Act
        boss.setSupervisoreSupervisionato(supervisor1);
        boss.setSupervisoreSupervisionato(supervisor2);
        boss = facade.saveSupervisore(boss);

        // Assert
        assertNotNull(supervisor1.getId());
        assertNotNull(supervisor2.getId());
        assertNotNull(boss.getId());
        assertNotEquals(supervisor1.getId(), supervisor2.getId());
        assertNotEquals(supervisor1.getId(), boss.getId());

        Optional<Supervisore> found = facade.trovaPerId(supervisor1.getId());
        assertTrue(found.isPresent());
        assertEquals("Matteo", found.get().getNome());

        Optional<Supervisore> notFound = facade.trovaPerId(boss.getId() + 1);
        assertTrue(notFound.isEmpty());
    }

    @Test
    @Transactional
    void shouldFindAllSupervisors() {
        long initialCount = facade.conta();
        Supervisore s1 = creaSupervisore("Alice", "Verdi");
        Supervisore s2 = creaSupervisore("Bob", "Neri");

        List<Supervisore> all = facade.trovaTutti();

        assertTrue(all.size() >= initialCount + 2);
        assertEquals(s1.getId(), all.getFirst().getId());
        assertEquals(s2.getId(), all.getLast().getId());
    }

    @Test
    @Transactional
    void shouldDeleteSupervisor() {
        Supervisore supervisor = creaSupervisore("Carlo", "Blu");
        assertTrue(facade.esistePerId(supervisor.getId()));

        facade.eliminaPerId(supervisor.getId());
        assertFalse(facade.esistePerId(supervisor.getId()));
    }

    @Test
    @Transactional
    void shouldAssignAndRemoveSubordinates() {
        Supervisore boss = creaSupervisore("Boss", "Tech");
        Supervisore sub = creaSupervisore("Dev", "Uno");

        facade.assegnaSubordinato(boss.getId(), sub.getId());

        List<Supervisore> supervised = facade.trovaSupervisionati(boss.getId());
        assertEquals(1, supervised.size());
        assertTrue(facade.haSubordinati(boss.getId()));

        facade.rimuoviSubordinato(boss.getId(), sub.getId());

        // Assert: supervisore non ha più subordinati
        supervised = facade.trovaSupervisionati(boss.getId());
        assertEquals(0, supervised.size());
        assertFalse(facade.haSubordinati(boss.getId()));

        // Assert: non può assegnarsi a se stesso
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facade.assegnaSubordinato(boss.getId(), boss.getId())
        );

        System.out.println(exception.getMessage());
    }

    @Test
    @Transactional
    void shouldPreventComplexLoop() {
        Supervisore a = creaSupervisore("A", "Tech");
        Supervisore b = creaSupervisore("B", "Dev");
        Supervisore c = creaSupervisore("C", "Ops");

        facade.assegnaSubordinato(a.getId(), b.getId());
        facade.assegnaSubordinato(b.getId(), c.getId());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> facade.assegnaSubordinato(c.getId(), a.getId())
        );

        System.out.println(exception.getMessage());

        assertNotEquals(c.getSupervisore(), a.getSupervisore());
    }

    @Test
    @Transactional
    void shouldCountSupervised() {
        Supervisore boss = creaSupervisore("Boss", "X");
        Supervisore sub1 = creaSupervisore("Sub1", "A");
        Supervisore sub2 = creaSupervisore("Sub2", "B");

        facade.assegnaSubordinato(boss.getId(), sub1.getId());
        facade.assegnaSubordinato(boss.getId(), sub2.getId());

        long count = facade.contaSupervisionati(boss.getId());
        assertEquals(2, count);
        assertTrue(facade.haSubordinati(boss.getId()));
    }

    @Test
    @Transactional
    void shouldFindSupervisorsWithSubordinates() {
        Supervisore boss = creaSupervisore("Boss", "Uno");
        Supervisore sub = creaSupervisore("Sub", "Due");
        facade.assegnaSubordinato(boss.getId(), sub.getId());

        List<Supervisore> withSubordinates = facade.trovaConSubordinati();
        assertFalse(withSubordinates.isEmpty());
        assertTrue(withSubordinates.contains(boss));
    }

    @Test
    @Transactional
    void shouldFindRootSupervisors() {
        Supervisore root = creaSupervisore("Root", "Leader");
        Supervisore child = creaSupervisore("Child", "Member");
        facade.assegnaSubordinato(root.getId(), child.getId());

        List<Supervisore> roots = facade.trovaRoot();
        assertTrue(roots.contains(root));
        assertFalse(roots.contains(child));
    }
}
