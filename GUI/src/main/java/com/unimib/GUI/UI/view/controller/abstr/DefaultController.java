package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.view.utils.FieldsHandler;
import com.unimib.GUI.utils.UserSession;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class DefaultController {

    protected final UserSession userSession;

    /**
     * Backing storage for cleanup tasks, keyed by controller instance.
     */
    private final List<Runnable> cleanupTasks =
            new CopyOnWriteArrayList<>();

    protected DefaultController(UserSession userSession) {
        this.userSession = userSession;
    }

    protected UserSession userSession() {
        return userSession;
    }

    protected void showAlert(String title, String message) {
        ApplicationStateManager manager =
                userSession.applicationStateManager();

        Node[] alert = new Node[1];

        alert[0] = AlertDialog.createAlert(
                title,
                message,
                () -> manager.removeWindow(alert[0])
        );

        manager.addPopUp(alert[0]);
    }

    protected void showError(String message) {
        showAlert("Error", message);
    }

    protected void showSuccess(String message) {
        showAlert("Success", message);
    }

    protected boolean validate(Object value, String message) {
        if(FieldsHandler.validate(value)) {
            return true;
        }else{
            showError(message);
            return false;
        }
    }

    protected <T> void observeState(
            ReadOnlyObjectProperty<UIState<T>> property,
            Runnable onLoading,
            Consumer<T> onSuccess,
            Consumer<String> onError
    ) {
        ChangeListener<UIState<T>> listener = (_, _, state) -> {

            if (state == null) {
                return;
            }

            if (state.isLoading()) {
                if (onLoading != null) {
                    onLoading.run();
                }
                return;
            }

            if (state.getError() != null) {
                if (onError != null) {
                    onError.accept(state.getError());
                }
                return;
            }

            if (onSuccess != null) {
                onSuccess.accept(state.getData());
            }
        };

        property.addListener(listener);

        cleanupTasks.add(() -> property.removeListener(listener));
    }

    protected <T> void observeState(
            ReadOnlyObjectProperty<UIState<T>> property,
            Consumer<T> onSuccess,
            Consumer<String> onError
    ) {
        observeState(property, null, onSuccess, onError);
    }

    protected <T> void observeState(
            ReadOnlyObjectProperty<UIState<T>> property,
            Consumer<T> onSuccess
    ) {
        observeState(property, null, onSuccess, this::showError);
    }

    /**
     * Removes every listener registered through observeState().
     */
    public void disposeListeners() {
        cleanupTasks.forEach(Runnable::run);
        cleanupTasks.clear();
    }
}