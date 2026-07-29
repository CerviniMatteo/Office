package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.auth_state.AuthContainerController;
import com.unimib.GUI.UI.view.controller.impl.layout.auth_state.LoginController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class Auth extends VBox {
     AuthContainerController controller;
    public Auth() {
        super(8);
        controller = new AuthContainerController();
        FXMLUtilLoader.load(this, controller, "/components/Auth.fxml", "");
        setAlignment(Pos.CENTER);
    }
}