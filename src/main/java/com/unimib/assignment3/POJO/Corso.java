package com.unimib.assignment3.POJO;

import jakarta.persistence.*;
import java.util.Map;
import java.util.List;

@Entity
public class Corso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idCorso;

    private String nomeCorso;

    private int crediti;

    private int annoInsegnamento;
    private int semestreInsegnamento;

    @OneToMany(mappedBy = "professore", cascade = CascadeType.ALL)//se serve orphanRemoval = true
    @JoinColumn(name = "professore_id")//da decidere se metterlo o no
    private Map<Professore, Boolean> professori; //se il Boolean è true indica che il professore è titolare del corso

    protected Corso() {
        //costruttore vuoto per JPA
    }
    public Corso(String nomeCorso, int crediti, int annoInsegnamento, int semestreInsegnamento) {
        this.nomeCorso = nomeCorso;
        this.crediti = crediti;
        this.annoInsegnamento = annoInsegnamento;
        this.semestreInsegnamento = semestreInsegnamento;
    }
    public Corso(String nomeCorso, int crediti, int annoInsegnamento, int semestreInsegnamento, List<Professore> professori) {
        this.nomeCorso = nomeCorso;
        this.crediti = crediti;
        this.annoInsegnamento = annoInsegnamento;
        this.semestreInsegnamento = semestreInsegnamento;
        //inizializzare la mappa professori con i professori passati come parametro
        for (Professore professore : professori) {
            this.professori.put(professore, false); //impostando tutti come non titolari di default
        }
    }

    public Long getIdCorso() {
        return idCorso;
    }
    //setter di idCorso non necessaro in quanto generato automaticamente

    public String getNomeCorso() {
        return nomeCorso;
    }
    public void setNomeCorso(String nomeCorso) {
        this.nomeCorso = nomeCorso;
    }

    public int getCrediti() {
        return crediti;
    }
    public void setCrediti(int crediti) {
        this.crediti = crediti;
    }

    public int getAnnoInsegnamento() {
        return annoInsegnamento;
    }
    public void setAnnoInsegnamento(int annoInsegnamento) {
        this.annoInsegnamento = annoInsegnamento;
    }

    public int getSemestreInsegnamento() {
        return semestreInsegnamento;
    }
    public void setSemestreInsegnamento(int semestreInsegnamento) {
        this.semestreInsegnamento = semestreInsegnamento;
    }

    public Map<Professore, Boolean> getProfessori() {
        return professori;
    }
    public void setProfessori(Map<Professore, Boolean> professori) {
        this.professori = professori;
    }
    public void addProfessore(Professore professore, boolean isTitolare) {
        this.professori.put(professore, isTitolare);
    }
    public void modifyProfessoreRole(Professore professore, boolean isTitolare) {
        if(this.professori.containsKey(professore)) {
            this.professori.put(professore, isTitolare);
        }
    }
    public void removeProfessore(Professore professore) {
        this.professori.remove(professore);
    }

}
