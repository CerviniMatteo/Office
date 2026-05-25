package com.unimib.assignment3.view.components.abstr;

import com.unimib.assignment3.view.controller.abstr.DefaultController;
import com.unimib.assignment3.view.state.ApplicationStateManager;
import com.unimib.assignment3.view.utils.FXMLUtilLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class BasePopUpCard extends BorderPane {
    private final double MAX_WIDTH;
    private final double MAX_HEIGHT;
    protected final DefaultController controller;

    public BasePopUpCard(String fxmlResource, DefaultController controller, String style, double maxWidth, double maxHeight)
    {
        FXMLUtilLoader.load(this, controller, fxmlResource, style);
        MAX_WIDTH = maxWidth;
        MAX_HEIGHT = maxHeight;
        this.controller = controller;
    }

    public void showTaskPopup() {
        ApplicationStateManager.getInstance().showAsPopup(this, MAX_WIDTH, MAX_HEIGHT);
    }

    public void removeTaskPopup() {
        ApplicationStateManager.getInstance().removeWindow(this);
    }

    public DefaultController getController() {
        return controller;
    }
}
