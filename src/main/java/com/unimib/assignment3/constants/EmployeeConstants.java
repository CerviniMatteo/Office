package com.unimib.assignment3.constants;

import com.unimib.assignment3.enums.EmployeeRole;

public class EmployeeConstants {
    public static String NULL_MANAGER = "The employee requesting salaries cannot be NULL";
    public static String NOT_A_MANAGER = "The employee requesting salaries must be a " + EmployeeRole.MANAGER;
}
