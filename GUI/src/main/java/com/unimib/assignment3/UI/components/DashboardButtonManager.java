package com.unimib.assignment3.UI.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class DashboardButtonManager extends DashboardButton{
    private final ImageView openDashboardIcon;
    private final ImageView closeDashboardIcon;

    public DashboardButtonManager() {
        super("");
        setMaxSize(35,35);

        openDashboardIcon = createImageView("/images/left_panel_open.png", getMaxWidth(), getMaxHeight());
        closeDashboardIcon = createImageView("/images/left_panel_close.png", getMaxWidth(), getMaxHeight());

        setGraphic(closeDashboardIcon);
        hideBorder();
    }

    private ImageView createImageView(String path, double width, double height) {
        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            return iv;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ImageView();
        }
    }

    public void toggleIcon(boolean open) {
        setGraphic(open ? openDashboardIcon : closeDashboardIcon);
    }
}
