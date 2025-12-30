package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class SupervisoretegrationTest {

    @Autowired
    private Facade facade;

    @Test
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
    }
}
