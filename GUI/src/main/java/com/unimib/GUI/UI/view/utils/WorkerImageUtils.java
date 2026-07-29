package com.unimib.GUI.UI.view.utils;

import com.unimib.GUI.utils.ImageHelper;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;

/**
 * Utility methods for populating worker images into a GridPane.
 */
public final class WorkerImageUtils {

    private WorkerImageUtils() { /* utility */ }

    /**
     * Populate the provided GridPane with worker images (base64 strings) stored in the map.
     * If the currentWorkerId is present, its image will get a gold stroke applied.
     * This method mirrors the logic previously living inside TaskCardStartedController.
     *
     * @param workersGrid the GridPane to populate
     * @param assignedWorkers map of workerId -> base64 image
     * @param currentWorkerId the id of the current worker (can be null)
     * @param size image size in pixels
     * @param strokeWidth stroke width to apply for the highlighted worker
     */
    public static void populateWorkerImages(GridPane workersGrid,
                                            Map<Long, String> assignedWorkers,
                                            Long currentWorkerId,
                                            int size,
                                            double strokeWidth) {
        if (assignedWorkers == null || assignedWorkers.isEmpty()) {
            GridHelper.clearGrid(workersGrid);
            return;
        }
        GridHelper.ensureVisible(workersGrid, true);
        ImageHelper imageHelper = new ImageHelper();
        for (Map.Entry<Long, String> entry : assignedWorkers.entrySet()) {
            if (entry.getKey().equals(currentWorkerId)) {
                String updatedImage = imageHelper.addGoldStrokeAndReturnBase64(entry.getValue(), size, strokeWidth);
                entry.setValue(updatedImage);
            }
            GridHelper.addImageBase64(workersGrid, entry.getValue(), imageHelper, size, 3);
        }
    }

    public static String encodeFileAsBase64(File file) {
        Image image = new Image(file.toURI().toString());

        if (image.isError()) {
            throw new RuntimeException("Error occurred while loading image");
        }

        return Base64.getEncoder()
                .encodeToString(FileUtils.readBytes(file));
    }
}

