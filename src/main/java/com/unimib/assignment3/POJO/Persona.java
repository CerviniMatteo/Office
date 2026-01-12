package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

import java.util.Locale;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_persona")
    private Long id;

    private String nome;
    private String cognome;
    @Column(unique = true)
    private String email;

    public Persona() {}

    public Persona(String name, String surname) {
       setNome(name);
       setCognome(surname);
       setEmail(generateEmail(name,surname));
    }

    public Persona(String name, String surname, int emailCounter) {
        setNome(name);
        setCognome(surname);
        setEmail(generateEmail(name,surname,emailCounter));
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

    public void setCognome(String surname) {
        this.cognome = surname;
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

    public void setNome(String name) {
        this.nome = name;
    }


    private static String generateEmail(String name, String surname) {
        return name.toLowerCase(Locale.ROOT)+"."+surname.toLowerCase(Locale.ROOT)+"@example.com";
    }

    public static String generateEmail(String name, String surname, int emailCounter) {
        return generateEmail(name+emailCounter, surname);
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + nome + '\'' +
                ", surname='" + cognome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
