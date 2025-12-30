package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

import java.util.Locale;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JoinColumn(name = "id_persona")
    private Long id;

    private String nome;
    private String cognome;
    @Column(unique = true)
    private String email;

    public Persona() {}

    public Persona(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = generateEmail(nome,cognome);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    private static String generateEmail(String nome, String cognome) {
        return nome.toLowerCase(Locale.ROOT)+"."+cognome.toLowerCase(Locale.ROOT)+"@example.com";
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
