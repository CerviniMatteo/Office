package com.unimib.GUI.UI.state;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.ArrayDeque;
import java.util.Deque;

public class ApplicationStateManager {

    private final Deque<Node> navigationHistory;
    private final Deque<Node> forwardHistory;

    private final StackPane contentRoot;
    private final StackPane overlayRoot;

    private static volatile ApplicationStateManager INSTANCE;

    private ApplicationStateManager(StackPane contentRoot, StackPane overlayRoot) {
        this.navigationHistory = new ArrayDeque<>();
        this.forwardHistory = new ArrayDeque<>();
        this.contentRoot = contentRoot;
        this.overlayRoot = overlayRoot;
    }

    public static ApplicationStateManager getInstance(StackPane contentRoot, StackPane overlayRoot) {
        if (INSTANCE == null) {
            synchronized (ApplicationStateManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApplicationStateManager(contentRoot, overlayRoot);
                }
            }
        }
        return INSTANCE;
    }

    public static ApplicationStateManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "ApplicationStateManager non inizializzato. Chiamare prima getInstance(contentRoot, overlayRoot)."
            );
        }
        return INSTANCE;
    }

    public void addWindow(Node newWindow) {
        runOnFxThread(() -> {
            overlayRoot.getChildren().add(newWindow);
            bringToFront(newWindow);
            updateOverlayMouseTransparency();
        });
    }

    public void showAsPopup(Node popupNode, double maxWidth, double maxHeight) {
        runOnFxThread(() -> {
            if (popupNode instanceof javafx.scene.layout.Region region) {
                region.setMaxSize(maxWidth, maxHeight);
                region.setPrefSize(maxWidth, maxHeight);
            }

            popupNode.getProperties().put("blocksInput", true);
            overlayRoot.getChildren().add(popupNode);
            bringToFront(popupNode);
            updateOverlayMouseTransparency();
        });
    }

    public void addPopUp(Node newWindow) {
        runOnFxThread(() -> {
            newWindow.getProperties().put("blocksInput", true);
            overlayRoot.getChildren().add(newWindow);
            bringToFront(newWindow);
            updateOverlayMouseTransparency();
        });
    }

    public void removeWindow(Node window) {
        runOnFxThread(() -> {
            overlayRoot.getChildren().remove(window);
            contentRoot.getChildren().remove(window);
            updateOverlayMouseTransparency();
        });
    }

    public void replaceWindow(Node newWindow) {
        runOnFxThread(() -> {
            navigationHistory.addLast(newWindow);
            forwardHistory.clear();
            setContentWindow(newWindow);
        });
    }

    public void goBack() {
        runOnFxThread(() -> {
            if (navigationHistory.size() > 1) {
                Node current = navigationHistory.removeLast();
                forwardHistory.addLast(current);

                Node previous = navigationHistory.peekLast();
                if (previous != null) {
                    setContentWindow(previous);
                }
            }
        });
    }

    public void goForward() {
        runOnFxThread(() -> {
            if (!forwardHistory.isEmpty()) {
                Node next = forwardHistory.removeLast();
                navigationHistory.addLast(next);
                setContentWindow(next);
            }
        });
    }

    public Node getCurrentWindow() {
        return navigationHistory.peekLast();
    }

    private void setContentWindow(Node node) {
        contentRoot.getChildren().setAll(node);
        bringToFront(node);
        updateOverlayMouseTransparency();
    }

    private void bringToFront(Node node) {
        overlayRoot.toFront();
        node.toFront();
    }

    private void updateOverlayMouseTransparency() {
        boolean hasBlockingOverlays = overlayRoot.getChildren()
                .stream()
                .anyMatch(child ->
                        Boolean.TRUE.equals(child.getProperties().get("blocksInput"))
                );

        overlayRoot.setMouseTransparent(!hasBlockingOverlays);
    }

    private void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}