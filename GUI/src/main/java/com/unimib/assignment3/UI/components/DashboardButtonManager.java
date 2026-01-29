package com.unimib.assignment3.UI.components;

import javafx.scene.image.ImageView;
import static com.unimib.assignment3.UI.utils.SVGManager.svgToImageView;

public class DashboardButtonManager extends StyledButton {
    private final ImageView openDashboardIcon;
    private final ImageView closeDashboardIcon;

    public DashboardButtonManager() {
        setMaxSize(35,35);
        openDashboardIcon = svgToImageView("/images/left_panel_open.svg", getMaxWidth(), getMaxHeight(), "#F8E2D4");
        closeDashboardIcon = svgToImageView("/images/left_panel_close.svg", getMaxWidth(), getMaxHeight(), "#F8E2D4");
        setGraphic(closeDashboardIcon);
        getStyleClass().add("hide-border");
    }

    public void toggleIcon(boolean open) {
        setGraphic(open ? openDashboardIcon : closeDashboardIcon);
    }
}
