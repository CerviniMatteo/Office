package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import java.awt.*;

public class Dashboard extends VBox{

    private final StyledButton taskButton;
    private final StyledButton profileButton;
    private final StyledButton logoutButton;

    public Dashboard(DoubleBinding dashBoardSize) {
        super();
        prefWidthProperty().bind(dashBoardSize);
        toggleBorder(true);
        HBox.setMargin(this, new Insets(10));
        taskButton = new StyledButton("Task", "#F8E2D4", "F8E2D4");
        profileButton = new StyledButton("Profile", "#F8E2D4", "F8E2D4");
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        logoutButton = new StyledButton("Logout", "#F8E2D4", "F8E2D4");
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

    public void toggleBorder(boolean toggle){
        if(toggle) {
            setBorder(new Border(
                    new BorderStroke(
                            Paint.valueOf("#F8E2D4"),
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
