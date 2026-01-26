package com.unimib.assignment3.enums;

 /**
  * Enum representing different employee roles within the organization.
  * Each role is associated with a specific monthly salary.
  */
 public enum EmployeeRole {
     /**
      * Represents a Junior employee with a monthly salary of 2400.00.
      */
     JUNIOR(2400.00),

     /**
      * Represents a Senior employee with a monthly salary of 2800.00.
      */
     SENIOR(2800.00),

     /**
      * Represents a Senior Software Engineer with a monthly salary of 3000.00.
      */
     SENIOR_SW_ENGINEER(3000.00),

     /**
      * Represents a Software Architect with a monthly salary of 3600.00.
      */
     SW_ARCHITECT(3600.00),

     /**
      * Represents a Senior Software Architect with a monthly salary of 4000.00.
      */
     SENIOR_SW_ARCHITECT(4000.00),

     /**
      * Represents a Manager with a monthly salary of 7000.00.
      */
     MANAGER(7000.00);

     // The monthly salary associated with the employee role.
     private final double monthlySalary;

     /**
      * Constructor for the EmployeeRole enum.
      *
      * @param monthlySalary The monthly salary associated with the role.
      */
     EmployeeRole(double monthlySalary) {
         this.monthlySalary = monthlySalary;
     }

     /**
      * Retrieves the monthly salary associated with the employee role.
      *
      * @return The monthly salary.
      */
     public double getMonthlySalary() {
         return monthlySalary;
     }
 }