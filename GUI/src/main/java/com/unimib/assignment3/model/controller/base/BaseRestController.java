package com.unimib.assignment3.model.controller.base;

import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static com.unimib.assignment3.view.components.impl.custom.AlertDialog.showAlert;

public abstract class BaseRestController {

    // RestTemplate instance. If the BASE endpoint uses HTTPS on localhost we relax
    // SSL checks for development so self-signed certificates don't block requests.
    protected static final RestTemplate rest = createRestTemplate();

    private static RestTemplate createRestTemplate() {
        try {
                // Install a permissive trust manager for localhost (development only)
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }};

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                // Only accept hostname for localhost addresses
                HostnameVerifier hv = (hostname, _) -> "localhost".equalsIgnoreCase(hostname) || "127.0.0.1".equals(hostname);
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
            // If any error occurs while configuring SSL, fall back to default RestTemplate
            showAlert("Error", "Failed to configure relaxed SSL: " + e.getMessage());
        }
        return new RestTemplate();
    }

    // --- GET ---
    @Nullable
    protected <T> T getOne(String url, Class<T> type) {
        try {
            return rest.getForObject(url, type);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }

    @Nullable
    protected <T> T getMany(String url, ParameterizedTypeReference<T> type) {
        try {
            ResponseEntity<T> response = rest.exchange(url, HttpMethod.GET, null, type);
            return response.getBody();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }

    // --- POST ---
    protected <T> Task<T> postTask(String url, Object payload, Class<T> responseType) {
        return new Task<>() {
            @Override
            protected T call() {
                try {
                    HttpEntity<Object> entity = new HttpEntity<>(payload);
                    ResponseEntity<T> response = rest.exchange(url, HttpMethod.POST, entity, responseType);
                    return response.getBody();
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                    return null;
                }
            }
        };
    }

    protected Task<Void> postTask(String url, Object payload) {
        return new Task<>() {
            @Override
            protected Void call() {
                try {
                    HttpEntity<Object> entity = new HttpEntity<>(payload);
                    rest.exchange(url, HttpMethod.POST, entity, Void.class);
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                }
                return null;
            }
        };
    }
}