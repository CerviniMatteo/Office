package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.auth_state.AuthContainerController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.utils.UserSession;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class Auth extends VBox {
     AuthContainerController controller;
    public Auth(UserSession userSession) {
        super(8);
       controller = new AuthContainerController(userSession);
        FXMLUtilLoader.load(this, controller, "/components/Auth.fxml", "");
        setAlignment(Pos.CENTER);
    }
}