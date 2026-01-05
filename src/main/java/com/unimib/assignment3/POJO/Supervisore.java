// java
package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.Grado;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "supervisore")
public class Supervisore extends Dipendente {

    @ManyToOne
    @JoinColumn(name = "supervisore")
    private Supervisore supervisore;

    @OneToMany(
            mappedBy = "supervisore",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Supervisore> supervisoriSupervisionati = new ArrayList<>();

    @OneToMany(
            mappedBy = "supervisore",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Team> teamSupervisionato = new ArrayList<>();

    protected Supervisore() {
        super();
    }

    public Supervisore(String nome, String cognome) {
        super(nome, cognome, 2800.0, Grado.SW_ARCHITECT);
    }

    public Supervisore(String nome, String cognome, Double stipendio, Grado grado) {
        super(nome, cognome, stipendio, grado);
    }

    public Supervisore getSupervisore() {
        return supervisore;
    }

    public void setSupervisore(Supervisore supervisore) {
        this.supervisore = supervisore;
    }

    public List<Supervisore> getSupervisoriSupervisionati() {
        return supervisoriSupervisionati;
    }

    public void setSupervisoriSupervisionati(List<Supervisore> supervisoriSupervisionati) {
        this.supervisoriSupervisionati = supervisoriSupervisionati;
    }

    public void setSupervisoreSupervisionato(Supervisore supervisoreSupervisionato) {
        this.supervisoriSupervisionati.add(supervisoreSupervisionato);
    }

    public List<Team> getTeamSupervisionato() {
        return teamSupervisionato;
    }

    public void setTeamSupervisionato(List<Team> teamSupervisionati) {
        for (Team team : teamSupervisionati) {
            addTeam(team);
        }
    }

    public void setTeamSupervisionato(Team teamSupervisionato) {
        addTeam(teamSupervisionato);
    }

    private void addTeam(Team team) {
        if (team == null) return;
        if (!this.teamSupervisionato.contains(team)) {
            this.teamSupervisionato.add(team);
            team.setSupervisore(this);
        }
    }

    private void removeTeam(Team team) {
        if (team == null) return;
        if (this.teamSupervisionato.remove(team)) {
            team.setSupervisore(null);
        }
    }

    @Override
    public String toString() {
        return "Supervisore{" + super.toString() +
                ", supervisoriSupervisionati=" + supervisoriSupervisionati.stream().map(team -> team.getId().toString()).collect(Collectors.joining(", ")) +
                ", teamSupervisionato=" + teamSupervisionato.stream().map(team -> team.getIdTeam().toString()).collect(Collectors.joining(", ")) +
                '}';
    }
}
