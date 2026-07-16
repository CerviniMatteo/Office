package com.unimib.GUI.UI.view.components.impl.custom;

import com.unimib.GUI.UI.view.controller.impl.base.StyledButtonController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import javafx.scene.control.Button;

public class StyledButton extends Button {

    private final StyledButtonController controller;

    public StyledButton() {
        controller = new StyledButtonController(this);
            FXMLUtilLoader.load(this, controller, "/components/StyledButton.fxml", "styled-btn");
    }

    public StyledButtonController getController() {
        return controller;
    }
}