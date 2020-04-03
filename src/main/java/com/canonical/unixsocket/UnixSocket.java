package com.canonical.unixsocket;

import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;

import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.nio.channels.Channels;


public class UnixSocket extends Socket {
    final int bufferSize = 10240;
    private java.io.File path;
    private PrintWriter writer;
    private InputStreamReader reader;
    private UnixSocketChannel channel;
    private InputStream is;
    private OutputStream os;
    private boolean connected;

    public UnixSocket(String socket, int port) throws IOException {
        path = new java.io.File(socket);
        if (!path.exists()) {
            throw new IOException(String.format("Cannot find path '%s'", socket));
        }

        UnixSocketAddress address = new UnixSocketAddress(path);
        channel = UnixSocketChannel.open(address);

        reader = new InputStreamReader(Channels.newInputStream(channel));
        writer = new PrintWriter(Channels.newOutputStream(channel));

        is = new UnixSocketInputStream();
        os = new UnixSocketOutputStream();
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        connect(endpoint, 0);
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        connected = true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public synchronized void close() throws IOException {
        is.close();
        os.close();
        connected = false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return is;
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException {
        // no op
    }

    @Override
    public void setSoLinger(boolean on, int linger) throws SocketException {
        // no op
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        // no op
    }

    @Override
    public synchronized void setSendBufferSize(int size) throws SocketException {
        // no op
    }

    @Override
    public synchronized void setReceiveBufferSize(int size) throws SocketException {
        // no op
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return os;
    }

    class UnixSocketInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            byte[] bytes = new byte[1];
            int bytesRead = read(bytes);
            if (bytesRead == 0) {
                return -1;
            }
            return bytes[0] & 0xff;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            CharBuffer response = CharBuffer.allocate(bufferSize);
            String result = "";

            do {
                response.clear();
                reader.read(response);
                result += new String(response.array(), response.arrayOffset(), response.position());
            } while (response.position() == response.limit()
                    && response.get(response.limit() - 1) != '\n');

            byte[] data = result.getBytes();
            System.arraycopy(data, 0, b, off, data.length);
            return data.length;
        }
    }

    class UnixSocketOutputStream extends OutputStream {
        @Override
        public void write(int i) throws IOException {
            write(new byte[] {(byte) i});
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            String s = new String(b);
            writer.write(s, off, len);
        }

        @Override
        public void flush() throws IOException {
            writer.flush();
        }
    }
}
