package com.nhnacademy.aiot.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.nhnacademy.aiot.node.OutputNode;
import com.nhnacademy.aiot.pipe.Pipe;

class OutNode implements OutputNode {

    private static final AtomicInteger count = new AtomicInteger(0);

    private final int identify;
    private final Thread thread;
    private final Map<Integer, Pipe> inPipes;

    public OutNode() {
        identify = count.incrementAndGet();
        thread = new Thread(this);
        inPipes = new HashMap<>();
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

    private void process() {
        try {
            printMessageFromPipes();
        } catch (InterruptedException ignore) {
            thread.interrupt();
        }
    }

    private void printMessageFromPipes() throws InterruptedException {
        for (Pipe pipe : inPipes.values()) {
            printMessage(pipe);
        }
    }

    private void printMessage(Pipe pipe) throws InterruptedException {
        if (pipe.isEmpty()) {
            return;
        }
        System.out.println(identify + ": " + pipe.get().getString("value"));
    }

    @Override
    public void connectOut(int port, Pipe inPipe) {
        inPipes.put(port, inPipe);
    }

}
