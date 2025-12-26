package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Studente;
import com.unimib.assignment3.repository.StudenteRepository;
import com.unimib.assignment3.service.StudenteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(StudenteService.class)
@ActiveProfiles("test")
class StudenteIntegrationTest {

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private StudenteService studenteService;

    @Test
    void testCreazioneStudente() {
        Studente studente1 = new Studente("Matteo", "Cervini");
        studente1 = studenteRepository.saveAndFlush(studente1); // flush immediato

        assertNotNull(studente1.getId());
        System.out.println(studente1);

        Studente studente2 = new Studente("Mario", "Rossi");
        studente2 = studenteRepository.saveAndFlush(studente2);

        assertNotNull(studente2.getId());
        assertNotEquals(studente1.getId(), studente2.getId());
        System.out.println(studente2);
    }

    @Test
    void testEmailUnica() {
        Studente studente1 = new Studente("Matteo", "Cervini");
        studenteService.saveWithUniqueEmail(studente1);

        Studente studente2 = new Studente("Matilde", "Cervini");
        studenteService.saveWithUniqueEmail(studente2);

        Studente studente3 = new Studente("Marco", "Cervini");
        studenteService.saveWithUniqueEmail(studente3);

        System.out.println(studente1 + "\n" + studente2 + "\n" + studente3);
    }
}
