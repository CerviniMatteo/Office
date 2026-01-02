package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TeamIntegrationTest {
    
    @Autowired
    private Facade facade;

    @Test
    void testCreazioneTeam() {
        // Creazione di un supervisore
        Supervisore supervisore = new Supervisore("Matteo", "Cervini");
        supervisore = facade.saveSupervisore(supervisore);
        assertNotNull(supervisore.getId());
        System.out.println(supervisore);

        // Creazione di dipendenti
        Dipendente dipendente1 = new Dipendente("Andrea", "Aivaliotis");
        dipendente1 = facade.saveDipendente(dipendente1);
        assertNotNull(dipendente1.getId());
        System.out.println(dipendente1);
        Dipendente dipendente2 = new Dipendente("Le Yang", "Shi");
        dipendente2 = facade.saveDipendente(dipendente2);
        assertNotNull(dipendente2.getId());
        System.out.println(dipendente2);

        // Lista dei dipendenti
        List<Dipendente> dipendenti = List.of(dipendente1, dipendente2);

        // Creazione del team
        Team team = new Team(
                dipendenti,
                supervisore
        );
        team = facade.saveTeam(team);
        assertNotNull(team.getIdTeam());
        System.out.println(team);

        // Creazione di un altro team per verificare il salvataggio multiplo
        Team anotherTeam = new Team(
                supervisore
        );
        anotherTeam = facade.saveTeam(anotherTeam);
        assertNotNull(anotherTeam.getIdTeam());
        System.out.println(anotherTeam);
    }
}
