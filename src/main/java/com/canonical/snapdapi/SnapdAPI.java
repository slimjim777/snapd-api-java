package com.canonical.snapdapi;


import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.channels.Channels;


public class SnapdAPI {
    final String snapdSocket = "/run/snapd.socket";
    final int bufferSize = 8192;

    private UnixSocketChannel channel;
    private InputStreamReader reader;
    private PrintWriter writer;


    public SnapdAPI() throws IOException {
        java.io.File path = new java.io.File(snapdSocket);
        if (!path.exists()) {
            throw new IOException(String.format("Cannot find path '%s': is snapd installed and started?", snapdSocket));
        }

        UnixSocketAddress address = new UnixSocketAddress(path);
        this.channel = UnixSocketChannel.open(address);
        System.out.println("connected to " + channel.getRemoteSocketAddress());

        this.reader = new InputStreamReader(Channels.newInputStream(channel));
        this.writer = new PrintWriter(Channels.newOutputStream(channel));
    }

    public void getSystemInfo() throws IOException {
        System.out.println("Get system info...");
        String body = this.httpRequest("GET","/v2/system-info", "");

        System.out.println(body);
    }

    private String httpRequest(String method,String url,String body) throws IOException {
        // Make the request
        String data = new StringBuilder()
                .append(method)
                .append(" ")
                .append(url)
                .append(" HTTP/1.1\r\n")
                .append("Host: localhost\r\n")
                .append("User-Agent: agent\r\n")
                .append("Accept: */*\r\n")
                .append("\r\n")
                .append(body)
                .toString();
        this.write(data);

        // Get the response
        String response = this.read();

        // Get the body from the "headers\r\n\r\nbody" string
        // (there's probably a better way to parse the HTTP message!)
        String[] ss = response.split("\r\n\r\n");
        if (ss.length < 2) {
            return "";
        }
        return ss[1];
    }

    private void write(String payload) throws IOException {
        System.out.println(payload);
        this.writer.write(payload);
        this.writer.flush();
    }

    private String read() throws IOException {
        CharBuffer response = CharBuffer.allocate(this.bufferSize);
        String result = "";

        do {
            response.clear();
            this.reader.read(response);
            result += new String(response.array(), response.arrayOffset(), response.position());
        } while (response.position() == response.limit()
                && response.get(response.limit() - 1) != '\n');

        return result;
    }
}
