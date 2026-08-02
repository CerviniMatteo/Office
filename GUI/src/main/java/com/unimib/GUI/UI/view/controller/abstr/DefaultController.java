package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.view.utils.FieldsHandler;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public interface DefaultController{

    /**
     * Backing storage for cleanup tasks, keyed by controller instance.
     * WeakHashMap is used (not IdentityHashMap) so that entries are
     * automatically reclaimed once a controller is no longer referenced
     * elsewhere, avoiding a permanent memory leak. Since DefaultController
     * implementors don't override equals()/hashCode(), key comparison here
     * is effectively by reference (identity) anyway.
     *
     * This lives on the interface so implementing classes get
     * listener-cleanup tracking for free, without declaring any field.
     */
    Map<DefaultController, List<Runnable>> CLEANUP_TASKS =
            java.util.Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Returns the mutable list of cleanup tasks (listener removals)
     * registered by this controller instance via observeState().
     * No implementation needed by subclasses.
     */
    default List<Runnable> getCleanupTasks() {
        return CLEANUP_TASKS.computeIfAbsent(this, _ -> new CopyOnWriteArrayList<>());
    }

    default void showError(String message) {
        AlertDialog.showAlert("Error", message);
    }

    default void showSuccess(String message) {
        AlertDialog.showAlert("Success", message);
    }

    default boolean validate(Object value, String message) {
        return FieldsHandler.validate(value, message);
    }

    default <T> void observeState(
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
        getCleanupTasks().add(() -> property.removeListener(listener));
    }

    default <T> void observeState(
            ReadOnlyObjectProperty<UIState<T>> property,
            Consumer<T> onSuccess,
            Consumer<String> onError
    ) {
        observeState(property, null, onSuccess, onError);
    }

    default <T> void observeState(
            ReadOnlyObjectProperty<UIState<T>> property,
            Consumer<T> onSuccess
    ) {
        observeState(property, null, onSuccess, this::showError);
    }

    /**
     * Removes every listener registered through observeState() by this
     * controller instance. Must be called before handing off control to a
     * new controller instance (state switch) and/or on final teardown.
     */
    default void disposeListeners() {
        getCleanupTasks().forEach(Runnable::run);
        getCleanupTasks().clear();
    }
}