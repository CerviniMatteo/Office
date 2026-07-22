package com.unimib.GUI.UI.viewmodel;

import com.unimib.GUI.UI.state.UIState;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;

import java.util.function.Supplier;

public abstract class BaseViewModel {

    /**
     * Wraps a synchronous, blocking supplier in a Task and runs it through
     * execute(Task, ObjectProperty). Use this for repository methods that
     * just return a value (cache reads, disk reads, plain computations)
     * instead of building a Task by hand in the ViewModel - e.g. what
     * ChatViewModel.openChat() and TaskViewModel.fetchTasks()/fetchTask()
     * now do.
     */
    protected <T> void execute(Supplier<T> supplier, ObjectProperty<UIState<T>> state) {

        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return supplier.get();
            }
        };

        execute(task, state);
    }

    protected <T> void execute(
            Task<T> task,
            ObjectProperty<UIState<T>> state
    ) {

        state.set(UIState.loading());

        task.setOnSucceeded(_ ->
                state.set(UIState.success(task.getValue()))
        );

        task.setOnFailed(_ ->
                state.set(UIState.error(task.getException().getMessage()))
        );

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}