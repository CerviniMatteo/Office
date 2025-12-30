package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DipendentetegrationTest {

    @Autowired
    private Facade facade;

    @Test
    void testCreazioneStudente() {
        Dipendente dipendente1 = new Dipendente("Matteo", "Cervini");
        dipendente1 = facade.saveDipendente(dipendente1);

        assertNotNull(dipendente1.getId());
        System.out.println(dipendente1);

        Dipendente dipendente2 = new Dipendente("Andrea", "Aivaliotis");
        dipendente2 = facade.saveDipendente(dipendente2);

        assertNotNull(dipendente2.getId());
        assertNotEquals(dipendente1.getId(), dipendente2.getId());
        System.out.println(dipendente2);
    }
}
