package com.nhnacademy.aiot.node;

/**
 * 데이터를 입력하는 입력 노드 인터페이스입니다.
 */
public interface Inputable {

    /**
     * 출력 노드와 연결합니다.
     * 
     * @param outputPort 현재 노드의 출력 포트 번호입니다.
     * @param outputNode 데이터를 받는 노드입니다.
     * @param intputPort 데이터를 받는 노드의 입력 포트 번호입니다.
     */
    void connect(int outputPort, Target target);

}
