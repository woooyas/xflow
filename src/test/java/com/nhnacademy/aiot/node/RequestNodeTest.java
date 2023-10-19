package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

@SuppressWarnings("squid:S2187")
class RequestNodeTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        Pipe pipe1 = new Pipe();
        Pipe pipe2 = new Pipe();
        RequestNode requestNode = new RequestNode();

        requestNode.connectIn(0, pipe1);
        requestNode.connectOut(0, pipe2);

        requestNode.start();

        ServerSocket serverSocket = new ServerSocket(80);
        while (!Thread.interrupted()) {
            Message message = new Message(60_000);
            message.put("socket", serverSocket.accept());
            pipe1.put(message);
            Message message2 = pipe2.get();
            System.out.println(message2.get("socket") + message2.getString("request"));
            ((Socket) message2.get("socket")).getOutputStream()
                    .write("HTTP/1.1 200 OK\r\n".getBytes());
            ((Socket) message2.get("socket")).close();
        }
        serverSocket.close();
    }

}
