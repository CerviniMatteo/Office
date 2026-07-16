package com.unimib.GUI.UI.view.controller.impl.card;

import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.controller.abstr.TaskCardBaseController;
import com.unimib.GUI.model.dto.TaskDTO;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class TaskCardSkeletonController extends TaskCardBaseController{


    @FXML
    private Rectangle dateSkeleton;

    @FXML
    private Rectangle titleSkeleton;

    @FXML
    private Rectangle stateSkeleton;


    @FXML
    private BorderPane root;


    public TaskCardSkeletonController(TaskDTO taskDTO) {
        super(taskDTO);
    }


    @FXML
    public void initialize(){

        startAnimation();
    }


    private void startAnimation(){

        FadeTransition transition =
                new FadeTransition(
                    Duration.seconds(1),
                    root
                );

        transition.setFromValue(0.5);
        transition.setToValue(1);
        transition.setAutoReverse(true);
        transition.setCycleCount(
            FadeTransition.INDEFINITE
        );

        transition.play();
    }
}