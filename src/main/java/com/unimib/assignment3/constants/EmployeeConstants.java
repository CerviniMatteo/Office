package com.unimib.assignment3.constants;

import com.unimib.assignment3.enums.EmployeeRole;

public class EmployeeConstants {
    public static final String NULL_EMPLOYEE_ID = "The employee id cannot be null";
    public static final String NULL_EMPLOYEE = "The employee cannot be null";
    public static final String NULL_EMPLOYEES = "The employees list cannot be null";
    public static final String NOT_A_MANAGER = "The employee requesting salaries must be a " + EmployeeRole.MANAGER;
    public static final String  EMPLOYEE_ALREADY_ASSIGNED_TASK= "Employee already assigned to this task";
}
