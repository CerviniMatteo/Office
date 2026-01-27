package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

public class Dashboard extends VBox{

    private final DashboardButton taskButton;
    private final DashboardButton profileButton;
    private final DashboardButton logoutButton;

    public Dashboard(DoubleBinding dashBoardSize) {
        super();
        prefWidthProperty().bind(dashBoardSize);
        toggleBorder(true);
        HBox.setMargin(this, new Insets(10));
        taskButton = new DashboardButton("Task");
        profileButton = new DashboardButton("Profile");
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        logoutButton = new DashboardButton("Logout");
        getChildren().addAll(taskButton, profileButton, spacer, logoutButton);
    }

    public DashboardButton getTaskButton() {
        return taskButton;
    }

    public DashboardButton getLogoutButton() {
        return logoutButton;
    }

    public DashboardButton getProfileButton() {
        return profileButton;
    }

    public void toggleBorder(boolean toggle){
        if(toggle) {
            setBorder(new Border(
                    new BorderStroke(
                            Paint.valueOf("#4d067B"),
                            BorderStrokeStyle.SOLID,
                            new CornerRadii(10),
                            new BorderWidths(2)
                    )
            ));
        }else{
            setBorder(null);
        }
    }
}
