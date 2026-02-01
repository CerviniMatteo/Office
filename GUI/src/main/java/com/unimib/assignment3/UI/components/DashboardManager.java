package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class DashboardManager implements EventHandler<ActionEvent> {
    private final HBox dashboardContainer;
    private Dashboard dashboard;
    private DashboardButtonManager dashboardButtonManager;
    private Label buttonLabel;
    private static boolean dashboardOpen = true;
    public static int BUTTON_SIZE = 50;
    public static int ICON_SIZE = 24;

    public DashboardManager(DoubleBinding dashBoardSize) {
        dashboardContainer = new HBox();

        dashboard = new Dashboard(dashBoardSize, ICON_SIZE, BUTTON_SIZE) ;

        HBox.setHgrow(dashboard, Priority.ALWAYS);

        dashboardButtonManager = new DashboardButtonManager();
        dashboardButtonManager.setOnAction(this);
        dashboardButtonManager.setPadding(Insets.EMPTY);

        Region spacerB = new Region();
        spacerB.setPrefSize(10 ,10);
        Region spacerA = new Region();
        spacerA.setPrefSize(10 ,10);

        buttonLabel = new Label("Hide");
        buttonLabel.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
        buttonLabel.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
        buttonLabel.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
        buttonLabel.setWrapText(true);
        buttonLabel.setTextAlignment(TextAlignment.CENTER);
        buttonLabel.setAlignment(Pos.CENTER);
        buttonLabel.setFont(new Font("Verdana", 18));
        buttonLabel.setTextFill(Paint.valueOf("#F8E2D4"));
        buttonLabel.setMouseTransparent(true);
        buttonLabel.setFocusTraversable(false);
        buttonLabel.setPadding(Insets.EMPTY);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(dashboardButtonManager, buttonLabel);
        vBox.setAlignment(Pos.TOP_CENTER);

        dashboardContainer.getChildren().addAll(dashboard, spacerB, vBox, spacerA);
    }


    @Override
    public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() == dashboardButtonManager) {
            dashboardOpen = !dashboardOpen;
            if (dashboardOpen) {
                dashboardButtonManager.toggleIcon(false);
                buttonLabel.setText("Hide");
                if (!dashboardContainer.getChildren().contains(dashboard)) {
                    dashboardContainer.getChildren().addFirst(dashboard);
                }
            } else {
                dashboardButtonManager.toggleIcon(true);
                buttonLabel.setText("Show");
                dashboardContainer.getChildren().remove(dashboard);
            }
        }

    }

    public HBox getDashboardContainer() {
        return dashboardContainer;
    }
}
