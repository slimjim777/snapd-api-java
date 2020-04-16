package com.canonical.snapdapi;


import javafx.util.Pair;
import com.canonical.socketfactory.UnixConnectionSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class SnapdAPI {
    private final CloseableHttpClient httpclient;


    public SnapdAPI() throws URISyntaxException {
        Registry<ConnectionSocketFactory> socketFactoryRegistry;

        // Register a socket factory to handle the unix:// protocol
        socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new UnixConnectionSocketFactory(new URI("unix:///run/snapd.socket")))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();
    }

    public Pair<Integer, String> getSystemInfo() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost/v2/system-info");
        CloseableHttpResponse response;

        response = httpclient.execute(httpget);
        HttpEntity body = response.getEntity();
        Pair<Integer, String> resp = new Pair<>(response.getStatusLine().getStatusCode(), EntityUtils.toString(body));
        return resp;
    }
}
