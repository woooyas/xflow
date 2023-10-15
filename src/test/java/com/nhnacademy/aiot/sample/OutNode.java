package com.nhnacademy.aiot.sample;

import java.util.UUID;
import com.nhnacademy.aiot.node.OutputNode;
import com.nhnacademy.aiot.pipe.Pipe;

class OutNode implements OutputNode {

    private final UUID uuid;
    private final Thread thread;
    private Pipe inPipe;

    public OutNode() {
        uuid = UUID.randomUUID();
        thread = new Thread(this);
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
            printMessage();

        } catch (InterruptedException ignore) {
            thread.interrupt();
        }
    }

    private void printMessage() throws InterruptedException {
        if (inPipe == null) {
            return;
        }
        System.out
                .println("출력노드(" + uuid + ")가 '" + inPipe.get().getString("value") + "'를 처리하였습니다.");
    }

    @Override
    public void connectIn(int port, Pipe inPipe) {
        this.inPipe = inPipe;
    }

}
