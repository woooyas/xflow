package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

@SuppressWarnings("squid:S2187")
public class FunctionNodeTest {

    public static void main(String[] args) throws InterruptedException {
        Pipe pipe1 = new Pipe();
        Pipe pipe2 = new Pipe();
        FunctionNode functionNode = new FunctionNode(message -> {
            System.out.println(message.getString("value"));
            return message;
        });
        functionNode.connectIn(0, pipe1);
        functionNode.connectOut(0, pipe2);
        functionNode.start();

        Message message = new Message(60_000);
        message.put("value", "Hello, World!");
        pipe1.put(message);
    }

}
