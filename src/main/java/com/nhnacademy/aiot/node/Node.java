package com.nhnacademy.aiot.node;

/**
 * 스레드로 동작하는 노드 클래스입니다.
 */
public abstract class Node implements Runnable {

    private final Thread thread;

    /**
     * 노드를 생성합니다.
     * 
     * @param name 노드의 이름입니다.
     */
    protected Node(String name) {
        thread = new Thread(this, name);
    }

    /**
     * 노드를 실행합니다.
     */
    public void start() {
        thread.start();
    }

    /**
     * 노드를 종료합니다.
     */
    public void stop() {
        thread.interrupt();
    }

}
