package com.unimib.assignment3.web_socket_client;

import com.unimib.assignment3.constants.Rest;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Base class for WebSocket STOMP clients.
 * Handles SSL configuration, URL building, and STOMP client setup.
 * Subclasses provide the session handler via {@link #createSessionHandler()}.
 */
public abstract class AbstractWebSocketClientApp {

    /**
     * Subclasses return a StompSessionHandler that defines subscription and message handling.
     */
    protected abstract StompSessionHandler createSessionHandler();

    /**
     * Connects to the WebSocket server and starts the STOMP session.
     * @throws Exception if the connection fails
     */
    public void start() throws Exception {
        StandardWebSocketClient wsClient = new StandardWebSocketClient();
        SSLContext ssl = buildTrustAllSslContext();
        if (ssl != null) {
            wsClient.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", ssl);
            SSLParameters params = new SSLParameters();
            params.setEndpointIdentificationAlgorithm("");
            wsClient.getUserProperties().put("org.apache.tomcat.websocket.SSL_PARAMETERS", params);
        }

        WebSocketStompClient stompClient = new WebSocketStompClient(wsClient);
        stompClient.setMessageConverter(new StringMessageConverter());

        String wsUrl = buildWsUrl();
        System.out.println("Attempting WebSocket connect to: " + wsUrl);

        try {
            connect(stompClient, wsUrl);
        } catch (Exception ex) {
            System.err.println("WebSocket connection failed to " + wsUrl + ": " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Performs the actual async connect. Subclasses may override to capture the session.
     */
    protected void connect(WebSocketStompClient stompClient, String wsUrl) throws Exception {
        stompClient.connectAsync(wsUrl, createSessionHandler()).get();
    }

    // --- SSL ---

    /**
     * Builds a trust-all SSLContext if acceptAllCerts=true, otherwise returns null.
     * Uses X509ExtendedTrustManager to bypass both certificate validation and hostname
     * checking by Tomcat's AbstractTrustManagerWrapper.
     */
    private static SSLContext buildTrustAllSslContext() {
        if (!"true".equalsIgnoreCase(System.getProperty("unimib.acceptAllCerts", "false"))) {
            return null;
        }
        System.out.println("WARNING: Accepting all TLS certificates (unimib.acceptAllCerts=true)");
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509ExtendedTrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                public void checkServerTrusted(X509Certificate[] chain, String authType) { }
                public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) { }
                public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) { }
                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) { }
                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) { }
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }}, new SecureRandom());
            return sc;
        } catch (Exception ex) {
            System.err.println("Failed to build permissive SSL context: " + ex.getMessage());
            return null;
        }
    }

    // --- URL building ---

    /**
     * Derives the WebSocket URL from the REST base endpoint.
     */
    private static String buildWsUrl() {
        String wsUrl;
        wsUrl = Rest.BASE_ENDPOINT.replaceFirst("https://", "wss://");
        return wsUrl.endsWith("/") ? wsUrl + "ws" : wsUrl + "/ws";
    }
}