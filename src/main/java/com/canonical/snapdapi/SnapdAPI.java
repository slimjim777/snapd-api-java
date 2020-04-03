package com.canonical.snapdapi;


import com.canonical.unixsocket.UnixProtocolSocketFactory;
import javafx.util.Pair;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;

import java.io.*;


public class SnapdAPI {
    final String snapdSocket = "/run/snapd.socket";
    private HttpClient client;

    public SnapdAPI() throws IOException {
        // Initialize the HTTP client to handle the Snapd socket connection
        Protocol unix = new Protocol("unix", new UnixProtocolSocketFactory(snapdSocket), 80);
        client = new HttpClient();
        client.getHostConfiguration().setHost("localhost", 80, unix);
    }

    public Pair<Integer, String> getSystemInfo() throws IOException {
        GetMethod httpGet = new GetMethod("/v2/system-info");
        client.executeMethod(httpGet);

        Pair<Integer, String> resp = new Pair<>(httpGet.getStatusCode(), httpGet.getResponseBodyAsString());
        httpGet.releaseConnection();
        return resp;
    }
}
