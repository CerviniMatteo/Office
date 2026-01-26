package com.unimib.assignment3.constants;

public class TaskConstants {
    public static final String NULL_TASK_STATE = "TaskState cannot be null";
    public static final String TASK_NOT_FOUND = "Task not found";
    public static final String CANNOT_ASSIGN_DONE_TASK = "Isn't allowed to assign employees to tasks in DONE state";

    public static final String ONLY_STARTED_FROM_TO_BE_STARTED = "Is allowed only from TO BE STARTED to STARTED.";
    public static final String ONLY_DONE_FROM_STARTED = "Is allowed only from TO BE STARTED to STARTED. Use reset() to reset the task.";
    public static final String TASK_ALREADY_FINISHED = "The task is DONE, not allowed to change state. Use reset() to reset the task.";

    public static final String START_DATE_AFTER_END = "The start date cannot be after the end date.";
    public static final String END_DATE_BEFORE_START = "The end date cannot be before the start date.";

    public static final String NULL_TASK = "Task object cannot be null";
    public static final String NULL_TASK_ID = "Task ID cannot be null";
    public static final String NULL_EMPLOYEE_ID = "Employee ID cannot be null";
    public static final String NULL_DATE = "Date cannot be null";

    public static final String NULL_EMPLOYEE = "Employee object cannot be null";
    public static final String NULL_TASK_STATE_PARAM = "The provided TaskState cannot be null";
    public static final String NULL_TEAM_ID = "Team ID cannot be null";

    public static final String NEGATIVE_THRESHOLD = "Employee threshold cannot be negative";


}
