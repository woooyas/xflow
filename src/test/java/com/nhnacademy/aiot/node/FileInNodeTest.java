package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

@SuppressWarnings("squid:S2187")
public class FileInNodeTest {

    public static void main(String[] args) throws InterruptedException {
        Pipe pipe1 = new Pipe();
        Pipe pipe2 = new Pipe();
        FileInNode fileInNode = new FileInNode("index.html");
        fileInNode.connectIn(0, pipe1);
        fileInNode.connectOut(0, pipe2);
        fileInNode.start();

        pipe1.put(new Message(60_000));
        Message message = pipe2.get();
        System.out.println(message.getString("file"));
        System.out.println(message.getString("file").length());
    }

}
