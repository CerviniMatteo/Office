package com.unimib.assignment3.POJO;

import com.unimib.assignment3.enums.WorkerRole;
import jakarta.persistence.*;
import java.util.Locale;
import static com.unimib.assignment3.constants.CommonConstants.EMAIL_SUFFIX;

import org.apache.commons.validator.routines.EmailValidator;
/**
 * Abstract base class representing a person in the system.
 * This class is mapped to the "person" table and is extended by specific person types (e.g., Employee, Supervisor).
 * <p>
 * Email addresses are automatically generated based on name and surname, optionally with a counter for uniqueness.
 * </p>
 */
@Entity(name="worker")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Worker {

    /**
     * Unique identifier for the person.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long workerId;

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


    /** Monthly salary of the employee. */
    private double monthlySalary;

    /** Role of the employee. */
    private WorkerRole workerRole;

    /**
     * Default constructor for JPA.
     */
    protected Worker() {}

    /**
     * Constructs a person with a name and surname.
     * The email is automatically generated.
     *
     * @param name    the first name of the person
     * @param surname the surname of the person
     */
    public Worker(String name, String surname) {
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
     * @param workerRole the role of the worker
     */
    public Worker(String name, String surname, WorkerRole workerRole) {
        this(name, surname);
        setWorkerRole(workerRole);
        setMonthlySalary(workerRole.getMonthlySalary());
    }

    /**
     * Constructs a person with a name, surname, and email counter.
     * This allows generating unique emails when duplicates exist.
     *
     * @param name         the first name of the person
     * @param surname      the surname of the person
     * @param workerRole the role of the worker
     * @param monthlySalary the monthly salary of the worker
     */
    public Worker(String name, String surname, double monthlySalary, WorkerRole workerRole) {
        this(name, surname, workerRole);
        setMonthlySalary(monthlySalary);
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
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
     * Returns the monthly salary of the employee.
     *
     * @return monthly salary
     */
    public double getMonthlySalary() {
        return monthlySalary;
    }

    /**
     * Sets the monthly salary of the employee.
     *
     * @param monthlySalary the new monthly salary
     */
    public void setMonthlySalary(double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    /**
     * Returns the role of the employee.
     *
     * @return worker role
     */
    public WorkerRole getWorkerRole() {
        return workerRole;
    }

    /**
     * Sets the role of the employee.
     *
     * @param workerRole the new role
     */
    public void setWorkerRole(WorkerRole workerRole) {
        this.workerRole = workerRole;
    }


    /**
     * Generates an email based on name and surname.
     *
     * @param name    the first name
     * @param surname the surname
     * @return the generated email in the format "name.surname@example.com"
     */
    public static String generateEmail(String name, String surname) {
        String email =  name.toLowerCase(Locale.ROOT) + "." + surname.toLowerCase(Locale.ROOT) + EMAIL_SUFFIX;
        EmailValidator.getInstance().isValid(email);
        return email;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "workerId=" + workerId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", monthlySalary=" + monthlySalary +
                ", workerRole=" + workerRole +
                '}';
    }
}
