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

        // Creazione di dipendenti
        Dipendente dipendente1 = new Dipendente("Andrea", "Aivaliotis");
        dipendente1 = facade.saveEmployee(dipendente1);
        assertNotNull(dipendente1.getId());
        System.out.println(dipendente1);
        Dipendente dipendente2 = new Dipendente("Le Yang", "Shi");
        dipendente2 = facade.saveEmployee(dipendente2);
        assertNotNull(dipendente2.getId());
        System.out.println(dipendente2);

        // Lista dei dipendenti
        List<Dipendente> dipendenti = List.of(dipendente1, dipendente2);

        // Creazione del team
        Team team = new Team(
                dipendenti
        );
        team = facade.saveTeam(team);
        assertNotNull(team.getIdTeam());
        System.out.println(team);

        // Creazione di un altro team per verificare il salvataggio multiplo
        Dipendente dipendente3 = new Dipendente("Luca", "Rossi");
        dipendente3 = facade.saveEmployee(dipendente3);
        assertNotNull(dipendente3.getId());
        System.out.println(dipendente3);
        Team anotherTeam = new Team(
                List.of(dipendente3)
        );

        anotherTeam = facade.saveTeam(anotherTeam);
        assertNotNull(anotherTeam.getIdTeam());
        System.out.println(anotherTeam);

        Supervisore supervisore = facade.createSupervisor("Matteo", "Cervini");
        supervisore = facade.saveSupervisor(supervisore);
        assertNotNull(supervisore.getId());

        supervisore.setTeamSupervisionati(List.of(team, anotherTeam));
        System.out.println(supervisore);
        System.out.println(team+ "\n" + anotherTeam);

    }
}
