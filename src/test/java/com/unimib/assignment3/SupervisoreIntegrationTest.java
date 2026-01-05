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
class SupervisoreIntegrationTest {

    @Autowired
    private Facade facade;

    @Test
    @Transactional
    void testCreazioneSupervisore() {
        Supervisore supervisore1 = new Supervisore("Matteo", "Cervini");
        supervisore1 = facade.saveSupervisore(supervisore1);

        assertNotNull(supervisore1.getId());
        System.out.println(supervisore1);

        Supervisore supervisore2 = new Supervisore("Andrea", "Aivaliotis");
        supervisore2 = facade.saveSupervisore(supervisore2);

        assertNotNull(supervisore2.getId());
        assertNotEquals(supervisore1.getId(), supervisore2.getId());
        System.out.println(supervisore2);

        Supervisore capo = new Supervisore("Le Yang", "Shi");
        capo = facade.saveSupervisore(capo);
        capo.setSupervisoreSupervisionato(supervisore1);
        capo.setSupervisoreSupervisionato(supervisore2);
        capo = facade.saveSupervisore(capo);

        assertNotNull(capo.getId());
        assertNotEquals(supervisore1.getId(), capo.getId());
        System.out.println(capo);

        Optional<Supervisore> found = facade.trovaPerId(supervisore1.getId());
        assertTrue(found.isPresent());
        assertEquals("Matteo", found.get().getNome());

        Optional<Supervisore> notFound = facade.trovaPerId(capo.getId() + 1);
        assertTrue(notFound.isEmpty());
    }

    @Test
    @Transactional
    void testTrovaTutti() {
        long countInizio = facade.conta();
        Supervisore supervisore1 = facade.saveSupervisore(new Supervisore("Alice", "Verdi"));
        Supervisore supervisore2 = facade.saveSupervisore(new Supervisore("Bob", "Neri"));

        List<Supervisore> tutti = facade.trovaTutti();
        assertTrue(tutti.size() >= countInizio + 2);
        assertEquals(supervisore1.getId(), tutti.getFirst().getId());
        assertEquals(supervisore2.getId(), tutti.getLast().getId());
    }

    @Test
    @Transactional
    void testElimina() {
        Supervisore supervisore = facade.saveSupervisore(new Supervisore("Carlo", "Blu"));
        assertTrue(facade.esistePerId(supervisore.getId()));

        facade.eliminaPerId(supervisore.getId());
        assertFalse(facade.esistePerId(supervisore.getId()));
    }

    @Test
    @Transactional
    void testAssegnaRimuoviSubordinati() {
        Supervisore capo = facade.saveSupervisore(new Supervisore("Capo", "Tech"));
        Supervisore sub = facade.saveSupervisore(new Supervisore("Dev", "Uno"));

        facade.assegnaSubordinato(capo.getId(), sub.getId());
        List<Supervisore> supervisionati = facade.trovaSupervisionati(capo.getId());
        assertEquals(1, supervisionati.size());
        assertTrue(facade.haSubordinati(capo.getId()));

        facade.rimuoviSubordinato(capo.getId(), sub.getId());
        supervisionati = facade.trovaSupervisionati(capo.getId());
        assertEquals(0, supervisionati.size());
        assertFalse(facade.haSubordinati(capo.getId()));
    }

    @Test
    @Transactional
    void testContaSupervisionati() {
        Supervisore capo = facade.saveSupervisore(new Supervisore("Capo", "X"));
        Supervisore sub1 = facade.saveSupervisore(new Supervisore("Sub1", "A"));
        Supervisore sub2 = facade.saveSupervisore(new Supervisore("Sub2", "B"));

        facade.assegnaSubordinato(capo.getId(), sub1.getId());
        facade.assegnaSubordinato(capo.getId(), sub2.getId());

        long count = facade.contaSupervisionati(capo.getId());
        assertEquals(2, count);
        assertTrue(facade.haSubordinati(capo.getId()));
    }

    @Test
    @Transactional
    void testTrovaConSubordinati() {
        Supervisore capo = facade.saveSupervisore(new Supervisore("Capo", "Uno"));
        Supervisore sub = facade.saveSupervisore(new Supervisore("Sub", "Due"));
        facade.assegnaSubordinato(capo.getId(), sub.getId());

        List<Supervisore> conSubordinati = facade.trovaConSubordinati();
        assertFalse(conSubordinati.isEmpty());
        assertTrue(conSubordinati.contains(capo));
    }

    @Test
    @Transactional
    void testTrovaRoot() {
        Supervisore root = facade.saveSupervisore(new Supervisore("Root", "Leader"));
        Supervisore child = facade.saveSupervisore(new Supervisore("Child", "Member"));
        facade.assegnaSubordinato(root.getId(), child.getId());

        List<Supervisore> roots = facade.trovaRoot();
        assertTrue(roots.contains(root));
        assertFalse(roots.contains(child));
    }
}
