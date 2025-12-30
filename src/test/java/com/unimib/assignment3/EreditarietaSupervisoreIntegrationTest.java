package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EreditarietaSupervisoreIntegrationTest {

    @Test
    void supervisoreIsADipendente() {
        Supervisore supervisore = new Supervisore("Matteo", "Cervini");

        assertInstanceOf(Dipendente.class, supervisore);
    }

    @Test
    void supervisoreHasInheritedProperties() {
        Supervisore supervisore = new Supervisore("Le yang", "Shi");

        assertEquals("Le yang", supervisore.getNome());
        assertEquals("Shi", supervisore.getCognome());
        assertNotNull(supervisore.getStipendio());
        assertNotNull(supervisore.getGrado());
    }
}
