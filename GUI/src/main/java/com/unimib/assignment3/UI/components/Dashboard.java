package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;

import javafx.scene.layout.*;

public class Dashboard extends VBox {
    private final StyledButton taskButton;
    private final StyledButton profileButton;
    private final StyledButton logoutButton;

    public Dashboard(DoubleBinding dashBoardSize, int iconSize, int buttonSize) {
        super();
        prefWidthProperty().bind(dashBoardSize);
        getStyleClass().add("dashboard");
        taskButton = new StyledButton();
        taskButton.createDashboardStyledButtonContent("Add task",
                "M280-280h80v-400h-80v400Zm320-80h80v-320h-80v320ZM440-480h80v-200h-80v200ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm0-80h560v-560H200v560Zm0-560v560-560Z",
                iconSize);
        profileButton = new ProfileDashboardButton(iconSize, buttonSize);
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