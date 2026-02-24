package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.view.controller.impl.layout.LoginViewController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class Login extends VBox {

    FxApplication fxApplication;

    public Login(FxApplication fxApplication) {
        super(8);
        this.fxApplication = fxApplication;

        LoginViewController controller = new LoginViewController();
        controller.setFxApplication(fxApplication);
        FXMLUtilLoader.load(this, controller, "/components/Login.fxml", "");
        setAlignment(Pos.CENTER);
    }
}