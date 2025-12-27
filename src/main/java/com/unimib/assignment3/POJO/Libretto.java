package com.unimib.assignment3.POJO;

import java.util.Map;
import jakarta.persistence.*;

//aiva controlla tutto
public class Libretto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLibretto;

    @OneToOne
    @JoinColumn(name = "studente_id")
    private Studente studente;

    private String codiceCorsoDiLaurea;

    private double mediaVoti;

    @OneToMany(mappedBy = "corso", cascade = CascadeType.ALL)
    private Map<Corso, Integer> votiCorsi; // Corso e relativo voto

    protected Libretto() {
    }

    public Libretto(Studente studente) {
        this.studente = studente;
    }
    
    public Long getId() {
        return idLibretto;
    }
    
    public Studente getStudente() {
        return studente;
    }
    //non ho messo setter di studente perche' un libretto deve essere associato ad uno studente fin dalla creazione
    
    public String getCodiceCorsoDiLaurea() {
        return codiceCorsoDiLaurea;
    }
    public void setCodiceCorsoDiLaurea(String codiceCorsoDiLaurea) {
        this.codiceCorsoDiLaurea = codiceCorsoDiLaurea;
    }

    public double getMediaVoti() {
        return mediaVoti;
    }

}
