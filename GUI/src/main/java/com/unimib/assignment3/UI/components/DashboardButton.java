package com.unimib.assignment3.UI.components;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class DashboardButton {
    private final Button button;
    private final ImageView openDashboardIcon;
    private final ImageView closeDashboardIcon;

    public DashboardButton() {
        button = new Button();
        button.setMaxSize(35,35);

        openDashboardIcon = createImageView("/images/left_panel_open.png", button.getMaxWidth(), button.getMaxHeight());
        closeDashboardIcon = createImageView("/images/left_panel_close.png", button.getMaxWidth(), button.getMaxHeight());

        button.setGraphic(closeDashboardIcon);

        // Rimuovo focus, bordi, sfondo
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        button.setFocusTraversable(false);
        button.setPickOnBounds(true);


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

    public Button getButton() {
        return button;
    }

    public void toggleIcon(boolean open) {
        button.setGraphic(open ? openDashboardIcon : closeDashboardIcon);
    }
}
