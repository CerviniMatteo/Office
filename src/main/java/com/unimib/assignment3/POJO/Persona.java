package com.unimib.assignment3.POJO;

import jakarta.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;
    private String cognome;
    @Column(unique = true)
    private String emailIstituzionale;

    public Persona() {}

    public Persona(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmailIstituzionale() {
        return new String(emailIstituzionale);
    }

    public void setEmailIstituzionale(String emailIstituzionale) {
        this.emailIstituzionale = emailIstituzionale;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    abstract String generateEmail(String nome, String cognome);
}
