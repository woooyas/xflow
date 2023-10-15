package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.net.Socket;
import com.nhnacademy.aiot.pipe.Pipe;

@SuppressWarnings("squid:S2187")
public class ServerNodeTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        Pipe pipe = new Pipe();
        ServerNode serverNode = new ServerNode();
        serverNode.connectOut(0, pipe);
        serverNode.start();

        while (!Thread.interrupted()) {
            System.out.println((Socket) (pipe.get().get("socket")));
        }
    }

}
