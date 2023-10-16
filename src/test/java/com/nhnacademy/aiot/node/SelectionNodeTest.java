package com.nhnacademy.aiot.node;

import java.util.function.Function;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

@SuppressWarnings("squid:S2187")
public class SelectionNodeTest {

    public static void main(String[] args) throws InterruptedException {
        Pipe pipe1 = new Pipe();
        Pipe pipe2 = new Pipe();
        Pipe pipe3 = new Pipe();

        Function<Message, Integer> function = message -> {
            if (message.getInt("value") < 0)
                return 0;
            else if (message.getInt("value") % 2 == 0) {
                return 1;
            } else {
                return 123049821;
            }
        };
        SelectionNode SelectionNode = new SelectionNode(function);
        SelectionNode.connectIn(0, pipe1);
        SelectionNode.connectOut(0, pipe2);
        SelectionNode.connectOut(1, pipe3);
        SelectionNode.start();

        Message message = new Message(60_000);
        message.put("value", 10);
        Message message2 = new Message(60_000);
        message2.put("value", -5);
        Message message3 = new Message(60_000);
        message3.put("value", 11);
        pipe1.put(message);
        pipe1.put(message2);
        pipe1.put(message3);

        System.out.println(pipe2.get().getInt("value"));
        System.out.println(pipe3.get().getInt("value"));
    }

}
