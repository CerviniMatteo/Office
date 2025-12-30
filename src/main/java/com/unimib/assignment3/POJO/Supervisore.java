package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.Grado;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="supervisore")
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

    @OneToMany
    @JoinColumn(name = "team_supervisionato")
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

    public void setTeamSupervisionato(List<Team> teamSupervisionato) {
        this.teamSupervisionato = teamSupervisionato;
    }

    @Override
    public String toString() {
        return "Supervisore{" + super.toString() +
                ", supervisoriSupervisionati=" + supervisoriSupervisionati +
                ", teamSupervisionato=" + teamSupervisionato +
                '}';
    }
}
