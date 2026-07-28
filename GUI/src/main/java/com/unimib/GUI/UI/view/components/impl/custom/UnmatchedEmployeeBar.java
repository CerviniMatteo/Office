package com.unimib.GUI.UI.view.components.impl.custom;

import com.unimib.GUI.UI.view.controller.impl.base.UnmatchedEmployeeBarController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.model.dto.WorkerInfoDTO;
import javafx.scene.layout.HBox;

public class UnmatchedEmployeeBar extends HBox {

    private final UnmatchedEmployeeBarController controller;

    public UnmatchedEmployeeBar(WorkerInfoDTO userInfo) {
        super();
        this.controller = new UnmatchedEmployeeBarController();
        FXMLUtilLoader.load(this, controller, "/components/UnmatchedEmployeeBar.fxml", "unmatched-employee-bar");
        this.controller.setUserData(userInfo);
    }

    public UnmatchedEmployeeBarController getController() {
        return controller;
    }
}