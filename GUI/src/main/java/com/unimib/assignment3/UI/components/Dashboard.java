package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import java.awt.*;

public class Dashboard extends VBox{

    private final StyledButton taskButton;
    private final StyledButton profileButton;
    private final StyledButton logoutButton;

    public Dashboard(DoubleBinding dashBoardSize) {
        super();
        prefWidthProperty().bind(dashBoardSize);
        getStyleClass().add("dashboard");
        HBox.setMargin(this, new Insets(10));
        taskButton = new StyledButton();
        taskButton.setText("Tasks");
        profileButton = new StyledButton();
        profileButton.setText("Profile");
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        logoutButton = new StyledButton();
        logoutButton.setText("Logout");
        getChildren().addAll(taskButton, profileButton, spacer, logoutButton);
    }

    public StyledButton getTaskButton() {
        return taskButton;
    }

    public StyledButton getLogoutButton() {
        return logoutButton;
    }

    public StyledButton getProfileButton() {
        return profileButton;
    }
}
