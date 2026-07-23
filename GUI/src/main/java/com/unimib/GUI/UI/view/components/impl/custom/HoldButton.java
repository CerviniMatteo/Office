package com.unimib.GUI.UI.view.components.impl.custom;

import com.unimib.GUI.UI.view.controller.impl.base.HoldButtonController;
import javafx.geometry.Pos;

public class HoldButton extends StyledButton {

    private final HoldButtonController controller;

    public HoldButton() {
        controller = new HoldButtonController(this);
        setText("DELETE");
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.CENTER);
        getStyleClass().add("styled-btn-red-compact");
    }

    public void setOnHoldFinished(Runnable action) {
        controller.setOnHoldFinished(action);
    }

    public void setHoldDuration(double seconds) {
        controller.setHoldDuration(seconds);
    }

    public void setPressedColor(javafx.scene.paint.Color color) {
        controller.setPressedColor(color);
    }

    public void setHoldEndColor(javafx.scene.paint.Color color) {
        controller.setHoldEndColor(color);
    }
}