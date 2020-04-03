package com.canonical.unixsocket;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class UnixProtocolSocketFactory implements ProtocolSocketFactory {
    String socket;
    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return createSocket(socket, i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1, HttpConnectionParams httpConnectionParams) throws IOException, UnknownHostException, ConnectTimeoutException {
        return createSocket(socket, i);
    }

    // Use the socket file path instead of the provided host name
    @Override
    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return new UnixSocket(socket, i);
    }

    // Construct the socket and store the socket file path
    public UnixProtocolSocketFactory(String s) {
        socket = s;
    }
}
