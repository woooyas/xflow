package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.pipe.Pipe;

/**
 * 파이프에서 꺼낸 메세지를 처리하는 노드 타입입니다.
 */
public interface OutputNode extends Node {

    /**
     * 메세지를 꺼낼 수 있는 파이프와 연결합니다.
     * 
     * @param port 파이프를 연결할 노드의 출력 포트 번호입니다.
     * @param inPipe 연결할 파이프입니다.
     */
    public void connectIn(int port, Pipe inPipe);

}
