package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Professore;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProfessoreIntegrationTest{

    @Autowired
    Facade facade;
    @Test
    void professore_puo_avere_un_mentore() {
        Professore mentore = new Professore("Mario", "Rossi");
        Professore allievo = new Professore("Luca", "Bianchi");

        allievo.setMentore(mentore);

        mentore = facade.saveProfessore(mentore);
        allievo = facade.saveProfessore(allievo);

        Professore trovato = facade.findByIdProfessore(allievo.getId());

        assertNotNull(trovato.getMentore());
        assertNotEquals(mentore.getId(), allievo.getId());
        assertEquals(mentore.getId(), trovato.getMentore().getId());
        assertEquals("Mario", trovato.getMentore().getNome());
        assertEquals("Rossi", trovato.getMentore().getCognome());

        System.out.println(mentore);
        System.out.println(allievo);
    }

    @Test
    void professore_puo_essere_mentore_di_piu_professori() {
        Professore mentore = new Professore("Anna", "Verdi");

        Professore p1 = new Professore("Giulia", "Neri");
        Professore p2 = new Professore("Paolo", "Blu");

        p1 = facade.saveProfessore(p1);
        p2 = facade.saveProfessore(p2);

        mentore = facade.saveProfessore(mentore);
        mentore = facade.aggiungiAllievo(mentore, p1);
        mentore = facade.aggiungiAllievo(mentore, p2);

        assertEquals(2, mentore.getProfessoriSeguiti().size());
        assertEquals(p1.getMentore().getId(), mentore.getId());
        assertEquals(p2.getMentore().getId(), mentore.getId());
    }
}

