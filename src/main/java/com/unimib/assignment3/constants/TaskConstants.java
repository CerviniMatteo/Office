package com.unimib.assignment3.constants;

public class TaskConstants {
    public static final String NULL_TASK_STATE = "TaskState cannot be null";
    public static final String TASK_NOT_FOUND = "Task not found";
    public static final String CANNOT_ASSIGN_DONE_TASK = "Isn't allowed to assign employees to tasks in DONE state";

    // Costanti per le transizioni di stato
    public static final String ONLY_STARTED_FROM_TO_BE_STARTED = "Da DAINIZIARE si può passare solo a INIZIATO.";
    public static final String ONLY_DONE_FROM_STARTED = "Da INIZIATO si può passare solo a FINITO. Usa reset() per ricominciare.";
    public static final String TASK_ALREADY_FINISHED = "Il task è FINITO e non può più cambiare stato. Usa reset() per ricominciare.";

    // Costanti per la validazione date (viste precedentemente)
    public static final String START_DATE_AFTER_END = "La data di inizio non può essere successiva alla data di fine.";
    public static final String END_DATE_BEFORE_START = "La data di fine non può essere precedente alla data di inizio.";
}
