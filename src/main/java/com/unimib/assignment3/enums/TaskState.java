package com.unimib.assignment3.enums;

/**
 * The TaskState enum represents the  lifecycle stages of a task
 */
public enum TaskState {

    /**
     * The task has been created but work has not yet commenced.
     */
    TO_BE_STARTED,

    /**
     * The task is currently in progress and being handled by a worker or process.
     */
    STARTED,

    /**
     * The task has been successfully completed.
     */
    DONE
}
