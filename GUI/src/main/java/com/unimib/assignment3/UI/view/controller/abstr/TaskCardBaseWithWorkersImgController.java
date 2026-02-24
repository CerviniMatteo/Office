package com.unimib.assignment3.UI.view.controller.abstr;

import com.unimib.assignment3.UI.model.dto.TaskDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public abstract class TaskCardBaseWithWorkersImgController extends TaskCardBaseController{

    private final int IMG_SIZE = 50;
    private final double STROKE_WIDTH = 2.0;
    private final boolean isCurrentWorkerAssigned;

    @FXML
    private GridPane workersGrid;
    @FXML
    private Label dateLabel;

    public TaskCardBaseWithWorkersImgController(TaskDTO task) {
        super(task);
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
        return IMG_SIZE;
    }

    public double getStrokeWidth() {
        return STROKE_WIDTH;
    }
}
