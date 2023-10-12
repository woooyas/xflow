package com.nhnacademy.aiot.node;

public abstract class Node implements Runnable {

    private final Thread thread = new Thread(this);

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

}
