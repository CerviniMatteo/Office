package com.unimib.GUI.UI.view.utils;

import javafx.scene.control.TextInputControl;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import static com.unimib.GUI.UI.view.components.impl.custom.AlertDialog.showAlert;

public final class FieldsHandler {

    private FieldsHandler() {}

    public static boolean validate(Object value, String message) {
        boolean valid = switch (value) {
            case null -> false;
            case String s -> !s.trim().isEmpty();
            case TextInputControl text -> !text.getText().trim().isEmpty();
            case File file -> file.exists();
            case Collection<?> c -> !c.isEmpty();
            case Map<?, ?> m -> !m.isEmpty();
            default -> true;
        };

        if (!valid) {
            showAlert("Error", message);
        }

        return valid;
    }
}