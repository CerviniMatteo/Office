package com.unimib.assignment3.constants;

import com.unimib.assignment3.enums.WorkerRole;

public class SupervisorConstants {
    public static final String SUPERVISOR_NOT_FOUND = "Supervisor not found in the system";
    public static final String NULL_SUPERVISOR_ID = "The supervisor id cannot be null";
    public static final String NULL_SUPERVISOR = "The supervisor cannot be null";
    public static final String NULL_SUBORDINATES = "Subordinates cannot be null";
    public static final String CANNOT_HAVE_LOOP_SUBORDINATION = "Cannot have subordinate loops";
    public static final String SUPERVISOR_AT_LEAST_SW_ARCHITECT = "A supervisor must have at least the role of "+ WorkerRole.SW_ARCHITECT;
}