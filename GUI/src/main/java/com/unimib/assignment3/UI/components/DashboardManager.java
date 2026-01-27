package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;
import java.util.Stack;

public class DashboardManager implements EventHandler<ActionEvent> {
    private final HBox hbox;
    private Dashboard dashboard;
    private DashboardButtonManager dashboardButtonManager;
    List<Dashboard> dashBoardStack;

    public DashboardManager(DoubleBinding dashBoardSize) {
        hbox = new HBox();

        dashboard = new Dashboard(dashBoardSize);

        // Dashboard prende sempre spazio disponibile
        HBox.setHgrow(dashboard, Priority.ALWAYS);

        dashBoardStack = new Stack<>();
        dashBoardStack.add(dashboard);

        dashboardButtonManager = new DashboardButtonManager();
        dashboardButtonManager.addEventHandler(ActionEvent.ACTION, this);

        hbox.getChildren().addAll(dashboard, dashboardButtonManager);
    }


    @Override
    public void handle(ActionEvent actionEvent) {
        if(actionEvent.getSource() == dashboardButtonManager) {
            if(dashBoardStack.isEmpty()) {
                dashboardButtonManager.toggleIcon(false);
                dashBoardStack.add(dashboard);
                hbox.getChildren().addFirst(dashboard);
            }else{
                dashBoardStack.removeFirst();
                dashboardButtonManager.toggleIcon(true);
                hbox.getChildren().remove(dashboard);

            }
        }
    }

    public HBox getHbox() {
        return hbox;
    }
}
