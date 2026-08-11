package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public abstract class TaskCardBaseWithWorkersImgController extends TaskCardBaseController{

    private final boolean isCurrentWorkerAssigned;

    @FXML
    private GridPane workersGrid;
    @FXML
    private Label dateLabel;

    public TaskCardBaseWithWorkersImgController(TaskDTO task, UserSession userSession) {
        super(task, userSession);
        this.isCurrentWorkerAssigned = getCurrentTask().assignedWorkers().containsKey(getCurrentWorkerId());
    }

    @FXML
    protected void initialize() {
        super.initialize();
    }

    public Label getDateLabel() {return dateLabel;}

    public GridPane getWorkersGrid() {
        return workersGrid;
    }

    public boolean isCurrentWorkerAssigned() {return isCurrentWorkerAssigned;}

    public int getImgSize() {
        return 50;
    }

    public double getStrokeWidth() {
        return 2.0;
    }
}
