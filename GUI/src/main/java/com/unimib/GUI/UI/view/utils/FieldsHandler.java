package com.unimib.GUI.UI.view.utils;

import javafx.scene.control.TextInputControl;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public final class FieldsHandler {

    private FieldsHandler() {}

    public static boolean validate(Object value) {
        return switch (value) {
            case null -> false;
            case String s -> !s.trim().isEmpty();
            case TextInputControl text -> !text.getText().trim().isEmpty();
            case File file -> file.exists();
            case Collection<?> c -> !c.isEmpty();
            case Map<?, ?> m -> !m.isEmpty();
            default -> true;
        };
    }
}