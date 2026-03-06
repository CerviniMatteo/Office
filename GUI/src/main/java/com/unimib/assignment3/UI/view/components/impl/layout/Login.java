package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.view.controller.impl.layout.LoginViewController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class Login extends VBox {
    public Login() {
        super(8);

        LoginViewController controller = new LoginViewController();
        FXMLUtilLoader.load(this, controller, "/components/Login.fxml", "");
        setAlignment(Pos.CENTER);
    }
}