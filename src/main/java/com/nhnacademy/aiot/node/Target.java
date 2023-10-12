package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.message.Message;

/**
 * 데이터를 받을 노드와 포트가 담겨있는 타겟 클래스입니다.
 */
public class Target {

    private final Outputable outputNode;
    private final int inputPort;

    /**
     * 메세지를 받을 타겟을 설정합니다.
     * 
     * @param outputNode 메세지를 받을 노드입니다.
     * @param inputPort 메세지를 받을 노드의 입력 포트 번호입니다.
     */
    public Target(Outputable outputNode, int inputPort) {
        this.outputNode = outputNode;
        this.inputPort = inputPort;
    }

    /**
     * 타겟에게 메세지를 전송합니다.
     * 
     * @param message 전송할 메세지입니다.
     * @throws InterruptedException 인터럽트가 발생한 경우
     */
    public void add(Message message) throws InterruptedException {
        outputNode.add(inputPort, message);
    }

}
