package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.auth_state.RegistrationController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class Registration extends VBox {
    RegistrationController controller;
    public Registration() {
        super(8);

        controller = new RegistrationController();
        FXMLUtilLoader.load(this, controller, "/components/Registration.fxml", "");
        setAlignment(Pos.CENTER);
    }

    public RegistrationController getController() {
        return controller;
    }
}