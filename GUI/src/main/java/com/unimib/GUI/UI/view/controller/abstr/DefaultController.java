package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.view.utils.FieldsHandler;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.function.Consumer;

public interface DefaultController {

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
        property.addListener((_, _, state) -> {

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
        });
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
}