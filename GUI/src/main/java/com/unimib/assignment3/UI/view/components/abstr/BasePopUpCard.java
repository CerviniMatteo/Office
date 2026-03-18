package com.unimib.assignment3.UI.view.components.abstr;

import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import com.unimib.assignment3.UI.view.state.ApplicationStateManager;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
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

    public void showTaskPopup(Parent parentNode) {
        ApplicationStateManager.getInstance().showAsPopup(this, parentNode, MAX_WIDTH, MAX_HEIGHT);
    }

    public void removeTaskPopup() {
        ApplicationStateManager.getInstance().removeWindow(this);
    }

    public DefaultController getController() {
        return controller;
    }
}
