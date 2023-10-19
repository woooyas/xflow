package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

@SuppressWarnings("squid:S2187")
public class ResponseNodeTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        Pipe pipe = new Pipe();
        ResponseNode responseNode = new ResponseNode();
        responseNode.connectIn(0, pipe);
        responseNode.start();

        ServerSocket serverSocket = new ServerSocket(80);
        while (!Thread.interrupted()) {
            Socket socket = serverSocket.accept();
            Message message = new Message(60_000);
            message.put("socket", socket);
            message.put("response", "HTTP/1.1 200 OK");
            pipe.put(message);
        }
        serverSocket.close();
    }

}
