package com.unimib.GUI.UI.viewmodel;

import com.unimib.GUI.UI.state.UIState;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;

public abstract class BaseViewModel {

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