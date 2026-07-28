package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.auth_state.RegistrationController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class Registration extends VBox {
    public Registration() {
        super(8);

        RegistrationController controller = new RegistrationController();
        FXMLUtilLoader.load(this, controller, "/components/Registration.fxml", "");
        setAlignment(Pos.CENTER);
    }
}