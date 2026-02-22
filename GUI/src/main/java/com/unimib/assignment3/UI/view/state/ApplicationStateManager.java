package com.unimib.assignment3.UI.view.state;

import com.unimib.assignment3.UI.FxApplication;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the state and navigation of application windows, including
 * content windows and overlay windows. Supports back/forward navigation
 * similar to a browser.
 */
public class ApplicationStateManager {

    private final Deque<Node> windowsStack;
    private final Deque<Node> forwardStack;
    private final FxApplication application;
    private static volatile ApplicationStateManager INSTANCE;

    private ApplicationStateManager(FxApplication application) {
        this.windowsStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        this.application = application;
    }

    /**
     * Singleton getter for the ApplicationStateManager.
     *
     * @param application the FxApplication instance
     * @return the singleton instance of ApplicationStateManager
     */
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

    /**
     * Adds a new window to the overlay. Caller can specify whether the overlay
     * should block mouse input to the underlying content. Non-blocking overlays
     * (e.g. transient banners) should pass blocksInput=false.
     *
     * @param newWindow the window to add
     */
    public void addWindow(Node newWindow) {
        runOnFxThread(() -> {
            application.getOverlayRoot().getChildren().add(newWindow);
            bringToFront(newWindow);
            updateOverlayMouseTransparency();
        });
    }

    /**
     * Replaces the current content with a new window and updates navigation history.
     *
     * @param newWindow the new content window
     */
    public void replaceWindow(Node newWindow) {
        pushWindow(newWindow);
        runOnFxThread(() -> {
            setContentWindow(newWindow);
            updateOverlayMouseTransparency();
        });
    }

    /**
     * Removes a window from both content and overlay roots.
     *
     * @param window the window to remove
     */
    public void removeWindow(Node window) {
        runOnFxThread(() -> {
            application.getOverlayRoot().getChildren().remove(window);
            application.getContentRoot().getChildren().remove(window);
            updateOverlayMouseTransparency();
        });
    }

    /**
     * Navigate back in content history if available.
     */
    public void goBack() {
        runOnFxThread(() -> {
            if (windowsStack.size() > 1) {
                Node current = windowsStack.removeLast();
                forwardStack.addLast(current);

                Node previous = windowsStack.peekLast();
                if (previous != null) setContentWindow(previous);
            }
        });
    }

    /**
     * Navigate forward in content history if available.
     */
    public void goForward() {
        runOnFxThread(() -> {
            if (!forwardStack.isEmpty()) {
                Node next = forwardStack.removeLast();
                windowsStack.addLast(next);
                setContentWindow(next);
            }
        });
    }

    // ---------------------- Private Helper Methods ----------------------

    /**
     * Push a window to the back stack and clear forward stack (browser-like behavior).
     *
     * @param window the window to push
     */
    private void pushWindow(Node window) {
        windowsStack.addLast(window);
        forwardStack.clear();
    }

    /**
     * Sets the given node as the content window, clearing previous content
     * and ensuring overlay remains in front.
     *
     * @param node the node to set as content
     */
    private void setContentWindow(Node node) {
        application.getContentRoot().getChildren().setAll(node);
        bringToFront(node);
        updateOverlayMouseTransparency();
    }

    /**
     * Brings a node and the overlay root to the front.
     *
     * @param node the node to bring to front
     */
    private void bringToFront(Node node) {
        application.getOverlayRoot().toFront();
        node.toFront();
    }

    /**
     * Updates the overlay root's mouse transparency based on its children.
     * Only when at least one overlay child is explicitly marked as blocking
     * will the overlayRoot consume mouse events. This allows non-blocking
     * UI banners to be shown without preventing interaction with the content
     * beneath them.
     */
    private void updateOverlayMouseTransparency() {
        boolean hasBlockingOverlays = application.getOverlayRoot().getChildren().stream()
                .anyMatch(child -> Boolean.TRUE.equals(child.getProperties().get("blocksInput")));

        // If there are no blocking overlays, let mouse events pass through the overlay root
        application.getOverlayRoot().setMouseTransparent(!hasBlockingOverlays);
    }

    /**
     * Executes a runnable on the JavaFX Application thread, scheduling
     * with runLater if needed.
     *
     * @param runnable the code to run
     */
    private void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}