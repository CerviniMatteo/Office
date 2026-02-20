package com.unimib.assignment3.UI.state;

import com.unimib.assignment3.UI.FxApplication;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.Deque;
import java.util.ArrayDeque;

public class ApplicationStateManager {

    private final Deque<Node> windowsStack;
    private final FxApplication application;
    private static volatile ApplicationStateManager INSTANCE;

    private ApplicationStateManager(FxApplication application) {
        windowsStack = new ArrayDeque<>();
        this.application = application;
    }

    public static ApplicationStateManager getInstance(FxApplication application) {
        if (INSTANCE == null) {
            synchronized (ApplicationStateManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApplicationStateManager(application);
                }
            }
        }

        return INSTANCE;
    }

    private void pushWindow(Node newWindow) {
        windowsStack.addLast(newWindow);
    }

    private void popWindow() {
        if (windowsStack.size() > 1) {
            windowsStack.removeLast();
        }
    }

    private void updateOverlayMouseTransparency() {
        // Keep overlay root mouse transparent so overlays don't block clicks to underlying content.
        Runnable r = () -> application.getOverlayRoot().setMouseTransparent(true);
        if (Platform.isFxApplicationThread()) r.run(); else Platform.runLater(r);
    }

    public void addWindow(Node newWindow) {
        Runnable doAdd = () -> {
            application.getOverlayRoot().getChildren().add(newWindow);
            // ensure overlay container itself is in front of content
            application.getOverlayRoot().toFront();
            // bring the newly added node to front within overlay
            newWindow.toFront();
            updateOverlayMouseTransparency();
        };

        if (Platform.isFxApplicationThread()) {
            doAdd.run();
        } else {
            // Use nested runLater: first add during JavaFX pulse, then ensure z-order after layout
            Platform.runLater(() -> {
                doAdd.run();
                Platform.runLater(() -> {
                    application.getOverlayRoot().toFront();
                    newWindow.toFront();
                });
            });
        }
    }

    public void replaceWindow(Node newWindow) {
        // Replace the visible content in contentRoot
        pushWindow(newWindow);

        Runnable doReplace = () -> {
            application.getContentRoot().getChildren().clear();
            application.getContentRoot().getChildren().add(newWindow);
            // make sure overlay is still above content
            application.getOverlayRoot().toFront();
            newWindow.toFront();
            updateOverlayMouseTransparency();
        };

        if (Platform.isFxApplicationThread()) {
            doReplace.run();
        } else {
            Platform.runLater(() -> {
                doReplace.run();
                Platform.runLater(() -> application.getOverlayRoot().toFront());
            });
        }
    }

    public void removeWindow(Node window) {
        Runnable doRemove = () -> {
            application.getOverlayRoot().getChildren().remove(window);
            application.getContentRoot().getChildren().remove(window);
            updateOverlayMouseTransparency();
        };

        if (Platform.isFxApplicationThread()) {
            doRemove.run();
        } else {
            Platform.runLater(doRemove);
        }
    }
}
