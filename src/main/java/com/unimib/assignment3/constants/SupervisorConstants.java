package com.unimib.assignment3.constants;

import com.unimib.assignment3.enums.EmployeeRole;

public class SupervisorConstants {
    public static final String NULL_SUPERVISOR_ID = "The supervisor id cannot be null";
    public static final String NULL_SUPERVISOR = "The supervisor must exist";
    public static final String NULL_SUBORDINATES = "Subordinates cannot be null";
    public static final String CANNOT_HAVE_LOOP_SUBORDINATION = "Cannot have subordinate loops";
    public static final String SUPERVISOR_AT_LEAST_SW_ARCHITECT = "A supervisor must have at least the role of "+ EmployeeRole.SW_ARCHITECT;
}