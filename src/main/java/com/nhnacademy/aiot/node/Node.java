package com.nhnacademy.aiot.node;

/**
 * 실행할 수 있는 노드 타입입니다.
 */
public interface Node extends Runnable {

    /**
     * 노드를 실행합니다.
     */
    public void start();

    /**
     * 노드를 중단합니다.
     */
    public void stop();

}
