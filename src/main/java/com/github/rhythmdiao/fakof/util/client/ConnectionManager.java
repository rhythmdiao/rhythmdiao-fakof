package com.github.rhythmdiao.fakof.util.client;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author rhyth
 */

public enum ConnectionManager {
    INSTANCE;
    private PoolingHttpClientConnectionManager connectionManager;

    ConnectionManager() {
        try {
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(SSLContexts.custom().loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), (x509Certificates, s) -> true).build()))
                    .build();

            connectionManager = new PoolingHttpClientConnectionManager(registry);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    protected PoolingHttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
