package com.nhnacademy.aiot.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.node.InputNode;
import com.nhnacademy.aiot.pipe.Pipe;

class InNode implements InputNode {

    private final UUID uuid;
    private final Thread thread;
    private final Map<Integer, Pipe> outPipes;

    public InNode() {
        uuid = UUID.randomUUID();
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
            message.put("value", "입력노드(" + uuid + ")가 만든 메세지");
            pipe.put(message);
        }
    }

    @Override
    public void connectOut(int port, Pipe outPipe) {
        outPipes.put(port, outPipe);
    }

}
