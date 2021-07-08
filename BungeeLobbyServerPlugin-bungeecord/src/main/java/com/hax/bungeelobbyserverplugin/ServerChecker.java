package com.hax.bungeelobbyserverplugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerChecker {

    private DataOutputStream out;
    private DataInputStream in;

    private byte TABLISTBYTE = (byte) 254;

    public String checkServer(int port) {
        try {
            return (!setupSocket(port)) ? null : sendMessage(TABLISTBYTE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setupSocket(int port) throws IOException {
        try {
            Socket socket = new Socket("127.0.0.1", port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            return true;
        } catch (ConnectException ignore) {
            return false;
        }
    }

    public String sendMessage(byte msg) throws IOException {
        out.write(msg);

        byte[] bytes = in.readAllBytes();
        byte[] data = Arrays.copyOfRange(bytes, 4, bytes.length);
        return new String(data, StandardCharsets.UTF_16LE);
    }

}
