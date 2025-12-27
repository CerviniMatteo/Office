package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Professore extends Persona {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentore_id")
    private Professore mentore;

    @OneToMany(
            mappedBy = "mentore",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Professore> professoriSeguiti;

    @OneToMany
    @JoinColumn(name = "professore_id")
    private List<Corso> corsi;

    protected Professore() {
        super();
        this.professoriSeguiti = new ArrayList<>();
        this.corsi = new ArrayList<>();
    }

    public Professore(String nome, String cognome) {
        super(nome, cognome);
        setEmailIstituzionale(generateEmail(nome, cognome));
        this.professoriSeguiti = new ArrayList<>();
        this.corsi = new ArrayList<>();
    }

    @Override
    String generateEmail(String nome, String cognome) {
        return nome.toLowerCase() + "." + cognome.toLowerCase() + "@institute.com";
    }

    public Professore getMentore() {
        return mentore;
    }

    public void setMentore(Professore mentore) {
        this.mentore = mentore;
    }

    public List<Professore> getProfessoriSeguiti() {
        return professoriSeguiti;
    }

    public void setProfessoriSeguiti(List<Professore> professoriSeguiti) {
        this.professoriSeguiti = professoriSeguiti;
    }

    public void aggiungiAllievo(Professore allievo) {
        professoriSeguiti.add(allievo);
        allievo.mentore = this;
    }

    public void rimuoviAllievo(Professore allievo) {
        professoriSeguiti.remove(allievo);
        allievo.mentore = null;
    }

    public List<Corso> getCorsi() {
        return corsi;
    }

    public void setCorsi(List<Corso> corsi) {
        this.corsi = corsi;
    }

    @Override
    public String toString() {
        return "Professore{" +
                "id=" + getId() +
                ", nome=" + getNome() +
                ", cognome=" + getCognome() +
                '}';
    }

}
