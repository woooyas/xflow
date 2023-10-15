package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.pipe.Pipe;

/**
 * 처리한 메세지를 파이프에 넣는 노드 타입입니다.
 */
public interface InputNode extends Node {

    /**
     * 메세지를 넣을 수 있는 파이프와 연결합니다.
     * 
     * @param port 파이프를 연결할 노드의 입력 포트 번호입니다.
     * @param outPipe 연결할 파이프입니다.
     */
    public void connectOut(int port, Pipe outPipe);

}
