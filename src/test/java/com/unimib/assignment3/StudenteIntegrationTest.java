package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Studente;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class StudenteIntegrationTest{

    @Autowired
    private Facade facade;

    @Test
    void testCreazioneStudente() {
        Studente studente1 = new Studente("Matteo", "Cervini");
        studente1 = facade.saveStudente(studente1); // flush immediato

        assertNotNull(studente1.getId());
        System.out.println(studente1);

        Studente studente2 = new Studente("Mario", "Rossi");
        studente2 = facade.saveStudente(studente2);

        assertNotNull(studente2.getId());
        assertNotEquals(studente1.getId(), studente2.getId());
        System.out.println(studente2);
    }

    @Test
    void testEmailUnica() {
        Studente studente1 = new Studente("Matteo", "Cervini");
        facade.saveStudente(studente1);

        Studente studente2 = new Studente("Matilde", "Cervini");
        facade.saveStudente(studente2);

        Studente studente3 = new Studente("Marco", "Cervini");
        facade.saveStudente(studente3);

        System.out.println(studente1 + "\n" + studente2 + "\n" + studente3);
    }
}
