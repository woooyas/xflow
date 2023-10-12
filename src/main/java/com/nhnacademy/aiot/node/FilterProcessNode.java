package com.nhnacademy.aiot.node;

import java.util.function.Predicate;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

public class FilterProcessNode extends Node implements Inputable, Outputable {
    private final Predicate<Message> predicate;
    private final Pipe pipe;

    public FilterProcessNode(String name, Predicate<Message> predicate) {
        super();
        this.predicate = predicate;
        pipe = new Pipe();
    }

    boolean messageCheck(Message message) {
        return predicate.test(message);
    }

    public void filter(Message message) throws InterruptedException {
        if (messageCheck(message)) {
            pipe.add(message);
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    @Override
    public void add(int inputPort, Message message) throws InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public void connect(int outputPort, Target target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connect'");
    }

    public static void main(String[] args) {
        FilterProcessNode filterProcessNode = new FilterProcessNode(message -> message.getInt("value") < 5);
        Message message = new Message(100);
        message.append("value", 14);
        filterProcessNode.messageCheck(message);
    }
}
