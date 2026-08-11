package com.unimib.GUI.UI.view.controller.impl.layout.auth_state;

import com.unimib.GUI.UI.view.components.impl.layout.Login;
import com.unimib.GUI.UI.view.components.impl.layout.Registration;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.utils.UserSession;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class AuthContainerController extends DefaultController {

    @FXML
    private StackPane rootStack;

    private Login login;
    private Registration registration;

    public AuthContainerController(UserSession userSession) {
        super(userSession);
    }

    @FXML
    public void initialize() {

        login = new Login(userSession);
        registration = new Registration(userSession);

        rootStack.getChildren().addAll(login, registration);

        registration.setVisible(false);

        login.getController().setSwitchToRegistration(
                () -> flip(login, registration));
        registration.getController().setSwitchToLogin(
                () -> flip(registration, login));
    }

    private void flip(Node from, Node to) {

        RotateTransition first =
                new RotateTransition(Duration.millis(250), from);

        first.setAxis(Rotate.Y_AXIS);
        first.setFromAngle(0);
        first.setToAngle(90);

        RotateTransition second =
                new RotateTransition(Duration.millis(250), to);

        second.setAxis(Rotate.Y_AXIS);
        second.setFromAngle(-90);
        second.setToAngle(0);

        first.setOnFinished(e -> {

            from.setVisible(false);

            to.setVisible(true);
            to.setRotate(-90);

            second.play();
        });

        first.play();
    }
}