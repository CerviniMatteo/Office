package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.DashboardContainerController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.utils.UserSession;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class DashboardContainer extends BorderPane {
    private final DashboardContainerController controller;
    private final Sidebar sidebar;

    public DashboardContainer(UserSession userSession) {
        super();
        controller = new DashboardContainerController(userSession);
        sidebar = new Sidebar(userSession);
        controller.setSidebar(sidebar);

        FXMLUtilLoader.load(this, controller, "/components/DashboardContainer.fxml", null);
        controller.setRoot(this);
        setRight(sidebar);

        getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm()
        );
    }
}
