package com.unimib.assignment3.view.components.impl.layout;

import com.unimib.assignment3.view.controller.impl.layout.LoginViewController;
import com.unimib.assignment3.view.utils.FXMLUtilLoader;
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