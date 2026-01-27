package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DashboardManager implements EventHandler<ActionEvent> {
    private Dashboard dashboard;
    private DashboardButton dashboardButton;
    List<Dashboard> dashBoardStack;
    ReadOnlyDoubleProperty dashBoardSize;

    public  DashboardManager(ReadOnlyDoubleProperty widthProperty) {
        dashBoardSize = widthProperty;
        dashboard = new Dashboard(widthProperty.multiply(0.25));
        dashBoardStack = new Stack<>();
        dashBoardStack.add(dashboard);
        dashboardButton = new DashboardButton();
        dashboardButton.getButton().addEventHandler(ActionEvent.ACTION, this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if(actionEvent.getSource() == dashboardButton.getButton()) {
            if(dashBoardStack.isEmpty()) {
                getDashboardButton().toggleIcon(false);
                dashBoardStack.add(dashboard);
                dashboard.getDashboard().prefWidthProperty().bind(dashBoardSize.multiply(0.25));
                dashboard.toggleBorder(true);
            }else{
                dashBoardStack.removeFirst();
                getDashboardButton().toggleIcon(true);
                dashboard.getDashboard().prefWidthProperty().bind(dashBoardSize.multiply(0));
                dashboard.toggleBorder(false);
            }
        }
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public DashboardButton getDashboardButton() {
        return dashboardButton;
    }

    public void setDashboardButton(DashboardButton dashboardButton) {
        this.dashboardButton = dashboardButton;
    }
}
