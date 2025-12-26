package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

@Entity
public class Studente extends Persona {

    protected Studente() {
        super();
    }

    public Studente(String nome, String cognome) {
        super(nome, cognome);
        setEmailIstituzionale(generateEmail(nome, cognome));
    }

    @Override
    String generateEmail(String nome, String cognome) {
        return nome.toLowerCase().charAt(0)+ "." + cognome.toLowerCase() + "@institute.studenti.com";
    }


    @Override
    public String toString() {
        return "Studente{" +
                "nome=" + getNome() +
                ", cognome=" + getCognome() +
                ", matricolaStudente=" + getId() +
                ", emailIstituzionale='" + getEmailIstituzionale() + '\'' +
                '}';
    }
}
