package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.auth_state.LoginController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.utils.UserSession;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class Login extends VBox {
    LoginController controller;
    public Login(UserSession userSession) {
        super(8);
        controller = new LoginController(userSession);
        FXMLUtilLoader.load(this, controller, "/components/Login.fxml", "");
        setAlignment(Pos.CENTER);
    }

    public LoginController getController() {
        return controller;
    }
}