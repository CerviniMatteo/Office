package com.unimib.assignment3.POJO;

import jakarta.persistence.*;
import java.util.Locale;
import static com.unimib.assignment3.constants.CommonConstants.EMAIL_SUFFIX;

/**
 * Abstract base class representing a person in the system.
 * This class is mapped to the "person" table and is extended by specific person types (e.g., Employee, Supervisor).
 * <p>
 * Email addresses are automatically generated based on name and surname, optionally with a counter for uniqueness.
 * </p>
 */
@Entity(name="person")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Person {

    /**
     * Unique identifier for the person.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // @JoinColumn(name = "personId") // Not needed for @Id, only for relationships
    private Long personId;

    /**
     * First name of the person.
     */
    private String name;

    /**
     * Surname of the person.
     */
    private String surname;

    /**
     * Unique email of the person. Automatically generated from name and surname.
     */
    @Column(unique = true)
    private String email;

    /**
     * Default constructor for JPA.
     */
    public Person() {}

    /**
     * Constructs a person with a name and surname.
     * The email is automatically generated.
     *
     * @param name    the first name of the person
     * @param surname the surname of the person
     */
    public Person(String name, String surname) {
        setName(name);
        setSurname(surname);
        setEmail(generateEmail(name, surname));
    }

    /**
     * Constructs a person with a name, surname, and email counter.
     * This allows generating unique emails when duplicates exist.
     *
     * @param name         the first name of the person
     * @param surname      the surname of the person
     * @param emailCounter an integer appended to the name for email uniqueness
     */
    public Person(String name, String surname, int emailCounter) {
        setName(name);
        setSurname(surname);
        setEmail(generateEmail(name, surname, emailCounter));
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

    /**
     * Generates an email based on name and surname.
     *
     * @param name    the first name
     * @param surname the surname
     * @return the generated email in the format "name.surname@example.com"
     */
    private static String generateEmail(String name, String surname) {
        return name.toLowerCase(Locale.ROOT) + "." + surname.toLowerCase(Locale.ROOT) + EMAIL_SUFFIX;
    }

    /**
     * Generates a unique email based on name, surname, and a counter.
     *
     * @param name         the first name
     * @param surname      the surname
     * @param emailCounter a counter to ensure uniqueness
     * @return the generated email with the counter appended to the name
     */
    public static String generateEmail(String name, String surname, int emailCounter) {
        return generateEmail(name + emailCounter, surname);
    }

    @Override
    public String toString() {
        return "id=" + personId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
