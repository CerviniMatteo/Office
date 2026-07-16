package com.unimib.GUI.UI.view.state;

import com.unimib.GUI.FxApplication;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gestisce lo stato di navigazione dell'applicazione, mantenendo la cronologia
 * delle finestre di contenuto (back/forward) e le finestre overlay (popup, banner).
 *
 * <p>La navigazione tra finestre di contenuto funziona come un browser:
 * {@link #replaceWindow(Node)} aggiunge una nuova voce alla cronologia,
 * {@link #goBack()} e {@link #goForward()} la percorrono avanti e indietro,
 * e {@link #getCurrentWindow()} restituisce sempre la finestra attualmente visibile.
 *
 * <p>Le finestre overlay (popup bloccanti, banner non bloccanti) sono gestite
 * separatamente tramite {@link #addPopUp(Node)}, {@link #addWindow(Node)} e
 * {@link #showAsPopup(Node, double, double)}, e non influenzano la cronologia
 * di navigazione.
 *
 * <p>Implementa il pattern Singleton con double-checked locking perché esiste
 * un'unica radice UI ({@code contentRoot} / {@code overlayRoot}) nell'applicazione,
 * e lo stato di navigazione deve essere condiviso da qualsiasi controller senza
 * richiedere dependency injection esplicita lungo tutta la gerarchia.
 * Inizializzare con {@link #getInstance(FxApplication)} alla partenza
 * dell'applicazione; usare {@link #getInstance()} successivamente.
 */
public class ApplicationStateManager {

    /**
     * Cronologia delle finestre navigate. La finestra più recente (attualmente
     * visibile) si trova sempre in coda ({@code peekLast}). La testa corrisponde
     * alla finestra più vecchia nella sessione.
     */
    private final Deque<Node> navigationHistory;

    /**
     * Finestre rimosse dalla cronologia tramite {@link #goBack()} e recuperabili
     * con {@link #goForward()}. Viene azzerato ogni volta che si naviga verso
     * una nuova finestra, replicando il comportamento dei browser.
     */
    private final Deque<Node> forwardHistory;

    private final FxApplication application;
    private static volatile ApplicationStateManager INSTANCE;

    private ApplicationStateManager(FxApplication application) {
        this.navigationHistory = new ArrayDeque<>();
        this.forwardHistory = new ArrayDeque<>();
        this.application = application;
    }

    /**
     * Inizializza e restituisce il Singleton di {@code ApplicationStateManager}.
     * Deve essere chiamato una sola volta all'avvio dell'applicazione, prima
     * di qualsiasi chiamata a {@link #getInstance()}.
     *
     * <p>Thread-safe tramite double-checked locking su {@code volatile}.
     *
     * @param application l'istanza di {@link FxApplication} che espone
     *                    {@code contentRoot} e {@code overlayRoot}
     * @return l'istanza singleton di {@code ApplicationStateManager}
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
     * Restituisce il Singleton già inizializzato.
     *
     * @return l'istanza singleton di {@code ApplicationStateManager}
     * @throws IllegalStateException se {@link #getInstance(FxApplication)} non è
     *                               ancora stato chiamato
     */
    public static ApplicationStateManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "ApplicationStateManager non inizializzato. Chiamare prima getInstance(FxApplication)."
            );
        }
        return INSTANCE;
    }

    // ------------------------------------------------------------------ //
    //  Overlay (popup e banner)
    // ------------------------------------------------------------------ //

    /**
     * Aggiunge un nodo all'overlay senza bloccarne l'input del mouse.
     * Adatto a banner o notifiche transitori che non devono impedire
     * l'interazione con il contenuto sottostante.
     *
     * @param newWindow il nodo da aggiungere all'overlay
     */
    public void addWindow(Node newWindow) {
        runOnFxThread(() -> {
            application.getOverlayRoot().getChildren().add(newWindow);
            bringToFront(newWindow);
            updateOverlayMouseTransparency();
        });
    }

    /**
     * Visualizza un nodo come popup centrato con dimensioni massime specificate.
     * Il popup blocca l'input del mouse verso il contenuto sottostante.
     *
     * @param popupNode il nodo da visualizzare come popup
     * @param maxWidth  larghezza massima del popup in pixel
     * @param maxHeight altezza massima del popup in pixel
     */
    public void showAsPopup(Node popupNode, double maxWidth, double maxHeight) {
        runOnFxThread(() -> {
            if (popupNode instanceof javafx.scene.layout.Region region) {
                region.setMaxSize(maxWidth, maxHeight);
                region.setPrefSize(maxWidth, maxHeight);
            }
            popupNode.getProperties().put("blocksInput", true);
            application.getOverlayRoot().getChildren().add(popupNode);
            bringToFront(popupNode);
            updateOverlayMouseTransparency();
        });
    }

    /**
     * Aggiunge un nodo all'overlay marcandolo come bloccante per l'input del mouse.
     * A differenza di {@link #addWindow(Node)}, impedisce l'interazione con
     * qualsiasi elemento sotto l'overlay finché il popup è visibile.
     *
     * @param newWindow il nodo da aggiungere come overlay bloccante
     */
    public void addPopUp(Node newWindow) {
        runOnFxThread(() -> {
            newWindow.getProperties().put("blocksInput", true);
            application.getOverlayRoot().getChildren().add(newWindow);
            bringToFront(newWindow);
            updateOverlayMouseTransparency();
        });
    }

    /**
     * Rimuove un nodo sia dall'overlay che dal contenuto principale.
     * Non ha effetto se il nodo non è presente in nessuno dei due.
     *
     * @param window il nodo da rimuovere
     */
    public void removeWindow(Node window) {
        runOnFxThread(() -> {
            application.getOverlayRoot().getChildren().remove(window);
            application.getContentRoot().getChildren().remove(window);
            updateOverlayMouseTransparency();
        });
    }

    // ------------------------------------------------------------------ //
    //  Navigazione contenuto (browser-like)
    // ------------------------------------------------------------------ //

    /**
     * Sostituisce la finestra di contenuto corrente con {@code newWindow},
     * aggiungendola alla cronologia di navigazione e azzerando il
     * {@link #forwardHistory forward history} (comportamento browser).
     *
     * <p>Dopo la chiamata, {@link #getCurrentWindow()} restituisce {@code newWindow}.
     *
     * @param newWindow la nuova finestra di contenuto da visualizzare
     */
    public void replaceWindow(Node newWindow) {
        runOnFxThread(() -> {
            navigationHistory.addLast(newWindow);
            forwardHistory.clear();
            setContentWindow(newWindow);
        });
    }

    /**
     * Torna alla finestra precedente nella cronologia di navigazione, se disponibile.
     * La finestra corrente viene spostata nel {@link #forwardHistory forward history}
     * e potrà essere recuperata con {@link #goForward()}.
     *
     * <p>Non ha effetto se la cronologia contiene una sola voce (nessun precedente).
     */
    public void goBack() {
        runOnFxThread(() -> {
            if (navigationHistory.size() > 1) {
                Node current = navigationHistory.removeLast();
                forwardHistory.addLast(current);

                Node previous = navigationHistory.peekLast();
                if (previous != null) setContentWindow(previous);
            }
        });
    }

    /**
     * Avanza alla finestra successiva nel forward history, se disponibile.
     * Ha effetto solo dopo una o più chiamate a {@link #goBack()} senza
     * che nel frattempo sia stata navigata una nuova finestra con
     * {@link #replaceWindow(Node)}.
     */
    public void goForward() {
        runOnFxThread(() -> {
            if (!forwardHistory.isEmpty()) {
                Node next = forwardHistory.removeLast();
                navigationHistory.addLast(next);
                setContentWindow(next);
            }
        });
    }

    /**
     * Restituisce la finestra di contenuto attualmente visualizzata.
     *
     * @return il nodo corrente, oppure {@code null} se non è ancora stata
     *         impostata alcuna finestra di contenuto
     */
    public Node getCurrentWindow() {
        return navigationHistory.peekLast();
    }

    // ------------------------------------------------------------------ //
    //  Metodi privati di supporto
    // ------------------------------------------------------------------ //

    /**
     * Imposta {@code node} come unico figlio del {@code contentRoot},
     * portandolo in primo piano e aggiornando la trasparenza dell'overlay.
     *
     * @param node il nodo da impostare come contenuto principale
     */
    private void setContentWindow(Node node) {
        application.getContentRoot().getChildren().setAll(node);
        bringToFront(node);
        updateOverlayMouseTransparency();
    }

    /**
     * Porta l'overlay root e il nodo specificato in primo piano nella scena,
     * garantendo che l'overlay rimanga sempre sopra il contenuto.
     *
     * @param node il nodo da portare in primo piano
     */
    private void bringToFront(Node node) {
        application.getOverlayRoot().toFront();
        node.toFront();
    }

    /**
     * Aggiorna la trasparenza ai mouse event dell'overlay root in base ai
     * figli attualmente presenti. L'overlay blocca l'input solo se almeno
     * un figlio è marcato con la proprietà {@code "blocksInput" = true}.
     *
     * <p>Questo consente di mostrare banner non bloccanti senza impedire
     * l'interazione con il contenuto sottostante.
     */
    private void updateOverlayMouseTransparency() {
        boolean hasBlockingOverlays = application.getOverlayRoot().getChildren().stream()
                .anyMatch(child -> Boolean.TRUE.equals(child.getProperties().get("blocksInput")));

        application.getOverlayRoot().setMouseTransparent(!hasBlockingOverlays);
    }

    /**
     * Esegue {@code runnable} sull'Application Thread di JavaFX.
     * Se il thread corrente è già l'FX thread, la esegue direttamente;
     * altrimenti la schedula tramite {@link Platform#runLater(Runnable)}.
     *
     * @param runnable il codice da eseguire sull'FX thread
     */
    private void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}