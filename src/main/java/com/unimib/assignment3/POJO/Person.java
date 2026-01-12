package com.unimib.assignment3.POJO;

import jakarta.persistence.*;
import java.util.Locale;
import static com.unimib.assignment3.constants.CommonConstants.EMAIL_SUFFIX;


@Entity(name="person")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JoinColumn(name = "personId")
    private Long personId;

    private String name;
    private String surname;
    @Column(unique = true)
    private String email;

    public Person() {}

    public Person(String name, String surname) {
       setName(name);
       setSurname(surname);
       setEmail(generateEmail(name,surname));
    }

    public Person(String name, String surname, int emailCounter) {
        setName(name);
        setSurname(surname);
        setEmail(generateEmail(name,surname,emailCounter));
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    private static String generateEmail(String name, String surname) {
        return name.toLowerCase(Locale.ROOT)+"."+surname.toLowerCase(Locale.ROOT)+ EMAIL_SUFFIX;
    }

    public static String generateEmail(String name, String surname, int emailCounter) {
        return generateEmail(name+emailCounter, surname);
    }

    @Override
    public String toString() {
        return  "id=" + personId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
