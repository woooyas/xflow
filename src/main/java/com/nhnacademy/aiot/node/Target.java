package com.nhnacademy.aiot.node;

/**
 * 데이터를 받을 노드와 포트가 담겨있는 타겟 클래스입니다.
 */
public class Target {

    private final Outputable outputNode;
    private final int inputPort;

    public Target(Outputable outputNode, int inputPort) {
        this.outputNode = outputNode;
        this.inputPort = inputPort;
    }

    /**
     * 노드를 가져옵니다.
     * 
     * @return 노드 객체를 반환합니다.
     */
    public Outputable getNode() {
        return outputNode;
    }

    /**
     * 노드의 포트 번호를 가져옵니다.
     * 
     * @return 노드의 포트 번호를 반환합니다.
     */
    public int getInputPort() {
        return inputPort;
    }

}
