package com.unimib.assignment3.UI.components;

import javafx.scene.shape.SVGPath;

import static com.unimib.assignment3.UI.components.DashboardManager.BUTTON_SIZE;

public class DashboardButtonManager extends StyledButton {
    private final SVGPath openDashboardIcon;
    private final SVGPath closeDashboardIcon;
    private static final double ICON_SIZE = 0.05;

    public DashboardButtonManager() {
        super();
        setMinSize(BUTTON_SIZE, BUTTON_SIZE);
        setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
        setMaxSize(BUTTON_SIZE, BUTTON_SIZE);

        openDashboardIcon = new SVGPath();
        openDashboardIcon.setContent("M500-640v320l160-160-160-160ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm120-80v-560H200v560h120Zm80 0h360v-560H400v560Zm-80 0H200h120Z");
        openDashboardIcon.getStyleClass().add("icon");
        openDashboardIcon.setScaleX(ICON_SIZE);
        openDashboardIcon.setScaleY(ICON_SIZE);

        closeDashboardIcon = new SVGPath();
        closeDashboardIcon.setContent("M660-320v-320L500-480l160 160ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm120-80v-560H200v560h120Zm80 0h360v-560H400v560Zm-80 0H200h120Z");
        closeDashboardIcon.getStyleClass().add("icon");
        closeDashboardIcon.setScaleX(ICON_SIZE);
        closeDashboardIcon.setScaleY(ICON_SIZE);

        setGraphic(closeDashboardIcon);
        getStyleClass().add("hide-border");
    }

    public void toggleIcon(boolean open) {
        setGraphic(open ? openDashboardIcon : closeDashboardIcon);
    }
}
