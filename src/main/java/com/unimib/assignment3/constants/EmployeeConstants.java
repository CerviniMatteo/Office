package com.unimib.assignment3.constants;

import com.unimib.assignment3.enums.EmployeeRole;

public class EmployeeConstants {
    public static String NULL_EMPLOYEE_ID = "The employee id cannot be null";
    public static String NULL_EMPLOYEE = "The employee cannot be null";
    public static String NULL_EMPLOYEES = "The employees list cannot be null";
    public static String NULL_EMPLOYEE_ROLE = "The EmployeeRole cannot be null";
    public static String NOT_A_MANAGER = "The employee requesting salaries must be a " + EmployeeRole.MANAGER;
}
