package com.nhnacademy.aiot.test;

import java.util.HashMap;
import java.util.Map;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.node.InputNode;
import com.nhnacademy.aiot.pipe.Pipe;

class InNode implements InputNode {

    private final Thread thread;
    private final Map<Integer, Pipe> outPipes;

    public InNode() {
        thread = new Thread(this);
        outPipes = new HashMap<>();
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            process();
        }
    }

    @SuppressWarnings("squid:S2925")
    private void process() {
        try {
            putMessageToPipes();
            Thread.sleep(1_000);
        } catch (InterruptedException ignore) {
            thread.interrupt();
        }
    }

    private void putMessageToPipes() throws InterruptedException {
        for (Pipe pipe : outPipes.values()) {
            Message message = new Message(0);
            message.put("value", "test");
            pipe.put(message);
        }
    }

    @Override
    public void connectIn(int port, Pipe outPipe) {
        outPipes.put(port, outPipe);
    }

}
