package com.unimib.assignment3.UI.components;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

public class Dashboard {

    private VBox dashboard;

    public Dashboard(DoubleBinding doubleBinding) {
        setDashboard(new VBox());
        getDashboard().prefWidthProperty().bind(doubleBinding);
        toggleBorder(true);
        HBox.setMargin(dashboard, new Insets(15));
    }

    public void toggleBorder(boolean toggle){
        if(toggle) {
            dashboard.setBorder(new Border(
                    new BorderStroke(
                            Paint.valueOf("#4d067B"),
                            BorderStrokeStyle.SOLID,
                            new CornerRadii(10),
                            new BorderWidths(2)
                    )
            ));
        }else{
            dashboard.setBorder(null);
        }
    }

    public VBox getDashboard() {
        return dashboard;
    }

    public void setDashboard(VBox dashboard) {
        this.dashboard = dashboard;
    }
}
